package cl.monsoon.s1next.activity;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.LoaderManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.SeekBar;

import cl.monsoon.s1next.Api;
import cl.monsoon.s1next.Config;
import cl.monsoon.s1next.R;
import cl.monsoon.s1next.fragment.AbsHttpGetFragment;
import cl.monsoon.s1next.fragment.PostListPagerFragment;
import cl.monsoon.s1next.model.Reply;
import cl.monsoon.s1next.model.mapper.ReplyWrapper;
import cl.monsoon.s1next.util.MathUtil;
import cl.monsoon.s1next.util.NetworkUtil;
import cl.monsoon.s1next.util.ToastHelper;
import cl.monsoon.s1next.widget.AsyncResult;
import cl.monsoon.s1next.widget.FragmentStatePagerAdapter;
import cl.monsoon.s1next.widget.HttpPostLoader;
import cl.monsoon.s1next.widget.InputFilterRange;

/**
 * An Activity representing a list of posts.
 * Similar to {@see ThreadListActivity}
 */
public final class PostListActivity extends AbsNavigationDrawerActivity implements PostListPagerFragment.OnPagerInteractionCallback, PostListPagerFragment.OnGetFormHashCallback, LoaderManager.LoaderCallbacks<AsyncResult<ReplyWrapper>> {

    public final static String ARG_THREAD_TITLE = "thread_title";
    public final static String ARG_THREAD_ID = "thread_id";
    public final static String ARG_POST_REPLIES = "post_replies";

    /**
     * The serialization (saved instance state) Bundle key representing
     * SeekBar's progress when page flip dialog is showing.
     */
    private static final String STATE_SEEKBAR_PROGRESS = "seek_bar_progress";
    private int mSeekBarProgress = -1;

    private BroadcastReceiver broadcastReceiver;

    private ViewPager mViewPager;
    /**
     * The {@link FragmentStatePagerAdapter} will provide
     * fragments for each of the pages of posts.
     */
    private PagerAdapter mAdapter;

    private CharSequence mThreadId;
    private CharSequence mTitle;
    private int mNumPages;

    private static final int LOADER_REPLY = 0;

    private String mMessageToReply;
    private Loader mReplyLoader;
    private String mFormHash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDrawerToggle.setDrawerIndicatorEnabled(false);

        // not works well
        //        // title has marquee effect if thread title is long
        //        // similar use to AbsNavigationDrawerActivity#showGlobalContext()
        //        final TextView title;
        //        final int count = mToolbar.getChildCount();
        //        for (int i = 0; i < count; i++) {
        //            final View view = mToolbar.getChildAt(i);
        //            if (view instanceof TextView) {
        //                title = (TextView) view;
        //                title.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        //                title.setMarqueeRepeatLimit(-1);
        //                title.setSelected(true);
        //                break;
        //            }
        //        }

        FrameLayout parent = (FrameLayout) findViewById(R.id.frame_layout);
        View.inflate(this, R.layout.activity_screen_slide, parent);

        mTitle = getIntent().getCharSequenceExtra(ARG_THREAD_TITLE);
        setTitle(mTitle);
        mThreadId = getIntent().getCharSequenceExtra(ARG_THREAD_ID);
        setCount(getIntent().getIntExtra(ARG_POST_REPLIES, 1));

        mViewPager = (ViewPager) findViewById(R.id.pager);
        mAdapter = new PostListPagerAdapter(getFragmentManager());
        mViewPager.setAdapter(mAdapter);

        if (savedInstanceState != null) {
            mSeekBarProgress = savedInstanceState.getInt(STATE_SEEKBAR_PROGRESS);
            if (mSeekBarProgress != -1) {
                showDialogSeekBar();
            }
            //TODO: 恢复回复帖子的对话框
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // register broadcast receiver to check whether Wi-Fi is enabled
        // when we need to download images
        if ((Config.getAvatarsDownloadStrategy() != Config.DownloadStrategy.NOT
                || Config.getImagesDownloadStrategy() != Config.DownloadStrategy.NOT)) {
            Config.setWifi(NetworkUtil.isWifiConnected());

            broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    Config.setWifi(NetworkUtil.isWifiConnected());
                }
            };

            IntentFilter intentFilter =
                    new IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION);
            registerReceiver(broadcastReceiver, intentFilter);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (broadcastReceiver != null) {
            unregisterReceiver(broadcastReceiver);
            broadcastReceiver = null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_post, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);

                return true;
            // show SeekBar to let user to flip page
            case R.id.menu_page_flip:
                showDialogSeekBar();
                break;
            case R.id.menu_reply:
                if (mFormHash != null) {
                    showDialogReply();
                } else {
                    //FIXME 应该改为载入帖子完成前，禁用回帖按钮
                    ToastHelper.showByResId(R.string.forbid_reply);
                }
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt(STATE_SEEKBAR_PROGRESS, mSeekBarProgress);
    }

    /**
     * 在界面上显示回复帖子对话框
     */
    private void showDialogReply() {
        View view = getLayoutInflater().inflate(R.layout.dialog_reply, mDrawerLayout, false);
        EditText replayMessage = (EditText) view.findViewById(R.id.reply_message);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);
        builder.setTitle(R.string.dialog_reply_title);
        builder.setNegativeButton(android.R.string.cancel, (dialog, which) -> dialog.dismiss());
        builder.setPositiveButton(android.R.string.ok, (dialog, which) -> {
            mMessageToReply = replayMessage.getText().toString();
            mReplyLoader = getLoaderManager().getLoader(LOADER_REPLY);
            if (mReplyLoader == null) {
                mReplyLoader = getLoaderManager().initLoader(LOADER_REPLY, null, this);
            } else {
                mReplyLoader.onContentChanged();
            }
            dialog.dismiss();
        });
        AlertDialog dialog = builder.create();
        dialog.show();
        // 获取按钮对象前必须调用show()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
        replayMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(replayMessage.getText())) {
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                } else {
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                }
            }
        });

    }

    private void showDialogSeekBar() {
        View view = getLayoutInflater().inflate(R.layout.dialog_seekbar, mDrawerLayout, false);

        if (mSeekBarProgress == -1) {
            mSeekBarProgress = mViewPager.getCurrentItem();
        }

        final SeekBar seekbar = (SeekBar) view.findViewById(R.id.seekbar);
        seekbar.setProgress(mSeekBarProgress);
        // seekBar is zero-based!
        seekbar.setMax(mNumPages - 1);

        final EditText valueView = (EditText) view.findViewById(R.id.value);
        valueView.setText(String.valueOf(mSeekBarProgress + 1));
        valueView.setEms(String.valueOf(mNumPages).length());
        // set EditText range filter
        valueView.setFilters(
                new InputFilter[]{new InputFilterRange(1, mNumPages)});
        valueView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    int value = Integer.parseInt(valueView.getText().toString());
                    if (value - 1 != seekbar.getProgress()) {
                        seekbar.setProgress(value - 1);
                    }
                } catch (NumberFormatException ignored) {

                }
            }
        });

        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mSeekBarProgress = progress;

                int value = -1;
                try {
                    value = Integer.parseInt(valueView.getText().toString());
                } catch (NumberFormatException ignored) {

                }

                if (progress + 1 != value) {
                    valueView.setText(String.valueOf(progress + 1));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);
        builder.setTitle(R.string.dialog_seekbar_title_page_flip);
        builder.setNegativeButton(
                getText(android.R.string.cancel),
                (dialog, which) -> mSeekBarProgress = -1
        );
        builder.setPositiveButton(
                getText(android.R.string.ok),
                (dialog, which) -> {
                    mSeekBarProgress = -1;

                    if (!TextUtils.isEmpty(valueView.getText())) {
                        mViewPager.setCurrentItem(seekbar.getProgress());
                    }
                }
        );
        builder.show();
    }

    /**
     * Implement {@link cl.monsoon.s1next.fragment.PostListPagerFragment.OnPagerInteractionCallback}.
     */
    @Override
    public void setCount(int i) {
        mNumPages = MathUtil.divide(i, Config.POSTS_PER_PAGE);

        if (mAdapter != null) {
            runOnUiThread(mAdapter::notifyDataSetChanged);
        }
    }

    @Override
    public Loader<AsyncResult<ReplyWrapper>> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case LOADER_REPLY:
                return new HttpPostLoader<>(this, Api.URL_REPLY_POST + mThreadId,
                        ReplyWrapper.class, Api.getReplyPostBuilder(mMessageToReply, mFormHash));
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<AsyncResult<ReplyWrapper>> loader, AsyncResult<ReplyWrapper> asyncResult) {
        if (asyncResult.exception != null) {
            AsyncResult.handleException(asyncResult.exception);
        } else {
            switch (loader.getId()) {
                case LOADER_REPLY:
                    ReplyWrapper wrapper = asyncResult.data;
                    Reply reply = wrapper.unwrap();
                    ToastHelper.showByText(reply.getMessage());
                    break;
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<AsyncResult<ReplyWrapper>> loader) {
    }

    @Override
    public void setFormHash(String formHash) {
        mFormHash = formHash;
    }


    /**
     * Returns a Fragment corresponding to one of the pages of posts.
     */
    private class PostListPagerAdapter extends FragmentStatePagerAdapter {

        private PostListPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            return PostListPagerFragment.newInstance(mThreadId, i + 1);
        }


        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            // TODO: We can't see thread page number sometimes because title is long,
            // so it's better to put a TextView in ToolBar to show thread page number
            // or make the title marquee.
            setTitle(mTitle + "  " + (position + 1));

            super.setPrimaryItem(container, position, object);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            ((AbsHttpGetFragment) object).destroyRetainedFragment();

            super.destroyItem(container, position, object);
        }

        @Override
        public int getCount() {
            return mNumPages;
        }
    }
}
