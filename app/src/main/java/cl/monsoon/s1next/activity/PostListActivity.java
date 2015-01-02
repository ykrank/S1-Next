package cl.monsoon.s1next.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
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
import android.widget.Toast;

import com.squareup.okhttp.RequestBody;

import cl.monsoon.s1next.Api;
import cl.monsoon.s1next.R;
import cl.monsoon.s1next.fragment.BaseFragment;
import cl.monsoon.s1next.fragment.PostListPagerFragment;
import cl.monsoon.s1next.model.Result;
import cl.monsoon.s1next.model.mapper.ResultWrapper;
import cl.monsoon.s1next.singleton.Config;
import cl.monsoon.s1next.singleton.User;
import cl.monsoon.s1next.util.MathUtil;
import cl.monsoon.s1next.util.NetworkUtil;
import cl.monsoon.s1next.util.ObjectUtil;
import cl.monsoon.s1next.util.StringHelper;
import cl.monsoon.s1next.util.ToastUtil;
import cl.monsoon.s1next.widget.AsyncResult;
import cl.monsoon.s1next.widget.FragmentStatePagerAdapter;
import cl.monsoon.s1next.widget.HttpGetLoader;
import cl.monsoon.s1next.widget.HttpPostLoader;
import cl.monsoon.s1next.widget.InputFilterRange;

/**
 * An Activity representing a list of posts.
 * Similar to {@see ThreadListActivity}
 */
public final class PostListActivity
        extends BaseActivity
        implements PostListPagerFragment.OnPagerInteractionCallback,
        View.OnClickListener {

    public static final String ARG_THREAD_TITLE = "thread_title";
    public static final String ARG_THREAD_ID = "thread_id";
    public static final String ARG_POST_REPLIES = "post_replies";

    public static final String ARG_SHOULD_GO_TO_LAST_PAGE = "should_go_to_last_page";

    public static final String ACTION_QUOTE = "quote";

    /**
     * The serialization (saved instance state) Bundle key representing
     * SeekBar's progress when page flip dialog is showing.
     */
    private static final String STATE_SEEKBAR_PROGRESS = "seekbar_progress";
    private int mSeekBarProgress = -1;

    private CharSequence mThreadId;
    private CharSequence mThreadTitle;
    private int mTotalPages;

    /**
     * The {@link FragmentStatePagerAdapter} will provide
     * fragments for each page of posts.
     */
    private PagerAdapter mAdapter;
    private ViewPager mViewPager;

    private MenuItem mMenuPageFlip;

    private BroadcastReceiver mWifiReceiver;

    private BroadcastReceiver mQuoteReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        enableWindowTranslucentStatus();
        setNavDrawerIndicatorEnabled(false);

        // not works well
        //        // Title has marquee effect if thread's title is long.
        //        TextView title;
        //        int count = mToolbar.getChildCount();
        //        for (int i = 0; i < count; i++) {
        //            View view = mToolbar.getChildAt(i);
        //            if (view instanceof TextView) {
        //                title = (TextView) view;
        //                title.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        //                title.setMarqueeRepeatLimit(-1);
        //                title.setSelected(true);
        //                break;
        //            }
        //        }

        mThreadTitle = getIntent().getCharSequenceExtra(ARG_THREAD_TITLE);
        setTitle(StringHelper.concatTitleWithPageNum(mThreadTitle, 1));
        mThreadId = getIntent().getCharSequenceExtra(ARG_THREAD_ID);
        setTotalPages(getIntent().getIntExtra(ARG_POST_REPLIES, 1));

        FrameLayout container = (FrameLayout) findViewById(R.id.frame_layout);
        View.inflate(this, R.layout.activity_screen_slide, container);

        setupFloatingActionButton(R.drawable.ic_menu_comment_white_24dp);

        mViewPager = (ViewPager) container.findViewById(R.id.pager);
        mAdapter = new PostListPagerAdapter(getFragmentManager());
        mViewPager.setAdapter(mAdapter);
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                showOrHideToolbarAndFab(true);
            }

            @Override
            public void onPageSelected(int position) {
                // TODO: We can't see thread page number sometimes because title is long,
                // so it's better to put a TextView in ToolBar to show thread page number
                // or make the title marquee.
                setTitle(StringHelper.concatTitleWithPageNum(mThreadTitle, position + 1));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        // set ViewPager to last page when true
        if (getIntent().getBooleanExtra(ARG_SHOULD_GO_TO_LAST_PAGE, false)) {
            mViewPager.setCurrentItem(mTotalPages - 1);
        }

        if (savedInstanceState != null) {
            mSeekBarProgress = savedInstanceState.getInt(STATE_SEEKBAR_PROGRESS);
            if (mSeekBarProgress != -1) {
                showPageFlipDialog();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Register broadcast receiver to check whether Wi-Fi is enabled
        // when we need to download images.
        if (Config.getAvatarsDownloadStrategy() != Config.DownloadStrategy.NOT
                || Config.getImagesDownloadStrategy() != Config.DownloadStrategy.NOT) {
            Config.setWifi(NetworkUtil.isWifiConnected());

            mWifiReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    Config.setWifi(NetworkUtil.isWifiConnected());
                }
            };

            IntentFilter intentFilter =
                    new IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION);
            registerReceiver(mWifiReceiver, intentFilter);
        }

        mQuoteReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                startReplyActivity(
                        intent.getCharSequenceExtra(ReplyActivity.ARG_QUOTE_POST_ID),
                        intent.getCharSequenceExtra(ReplyActivity.ARG_QUOTE_POST_COUNT));
            }
        };
        registerReceiver(mQuoteReceiver, new IntentFilter(ACTION_QUOTE));
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mWifiReceiver != null) {
            unregisterReceiver(mWifiReceiver);
            mWifiReceiver = null;
        }

        unregisterReceiver(mQuoteReceiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_post, menu);

        mMenuPageFlip = menu.findItem(R.id.menu_page_flip);
        prepareMenuPageFlip();

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.menu_favourites_add).setEnabled(User.isLoggedIn());

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // show SeekBar to let user to flip page
            case R.id.menu_page_flip:
                showPageFlipDialog();

                return true;
            case R.id.menu_favourites_add:
                ThreadFavouritesAddDialogFragment.newInstance(mThreadId)
                        .show(getFragmentManager(), ThreadFavouritesAddDialogFragment.TAG);

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt(STATE_SEEKBAR_PROGRESS, mSeekBarProgress);
    }

    /**
     * Disable flip page menu when {@link cl.monsoon.s1next.activity.PostListActivity#mTotalPages} = 1.
     */
    private void prepareMenuPageFlip() {
        if (mMenuPageFlip == null) {
            return;
        }

        if (mTotalPages == 1) {
            mMenuPageFlip.setEnabled(false);
        } else {
            mMenuPageFlip.setEnabled(true);
        }
    }

    private void showPageFlipDialog() {
        View view =
                getLayoutInflater().inflate(
                        R.layout.dialog_seekbar, (ViewGroup) findViewById(R.id.drawer_layout), false);

        if (mSeekBarProgress == -1) {
            mSeekBarProgress = mViewPager.getCurrentItem();
        }

        SeekBar seekbar = (SeekBar) view.findViewById(R.id.seekbar);
        seekbar.setProgress(mSeekBarProgress);
        // seekBar is zero-based!
        seekbar.setMax(mTotalPages - 1);

        EditText valueView = (EditText) view.findViewById(R.id.value);
        valueView.setText(String.valueOf(mSeekBarProgress + 1));
        valueView.setEms(String.valueOf(mTotalPages).length());
        // set EditText range filter
        valueView.setFilters(
                new InputFilter[]{new InputFilterRange(1, mTotalPages)});
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

        new AlertDialog.Builder(this)
                .setView(view)
                .setTitle(R.string.dialog_seekbar_title_page_flip)
                .setPositiveButton(
                        getText(android.R.string.ok),
                        (dialog, which) -> {
                            mSeekBarProgress = -1;

                            if (!TextUtils.isEmpty(valueView.getText())) {
                                mViewPager.setCurrentItem(seekbar.getProgress());
                            }
                        }
                )
                .setNegativeButton(
                        getText(android.R.string.cancel),
                        (dialog, which) -> mSeekBarProgress = -1
                ).show();
    }

    @Override
    public int getTotalPages() {
        return mTotalPages;
    }

    /**
     * Implement {@link cl.monsoon.s1next.fragment.PostListPagerFragment.OnPagerInteractionCallback}.
     */
    @Override
    public void setTotalPages(int i) {
        mTotalPages = MathUtil.divide(i, Config.POSTS_PER_PAGE);

        prepareMenuPageFlip();

        if (mAdapter != null) {
            runOnUiThread(mAdapter::notifyDataSetChanged);
        }
    }

    /**
     * Floating action button's {@link android.view.View.OnClickListener}.
     */
    @Override
    public void onClick(View v) {
        startReplyActivity(null, null);
    }

    void startReplyActivity(@Nullable CharSequence quotePostId, @Nullable CharSequence quotePostCount) {
        // show LoginPromptDialog if user hasn't logged in.
        if (!User.isLoggedIn()) {
            new LoginPromptDialog().show(getFragmentManager(), LoginPromptDialog.TAG);

            return;
        }

        Intent intent = new Intent(this, ReplyActivity.class);

        intent.putExtra(ReplyActivity.ARG_THREAD_TITLE, mThreadTitle);
        intent.putExtra(ReplyActivity.ARG_THREAD_ID, mThreadId);
        intent.putExtra(ReplyActivity.ARG_QUOTE_POST_ID, quotePostId);
        intent.putExtra(ReplyActivity.ARG_QUOTE_POST_COUNT, quotePostCount);

        startActivity(intent);
    }

    /**
     * Return a Fragment corresponding to one of the pages of posts.
     */
    private class PostListPagerAdapter extends FragmentStatePagerAdapter {

        private PostListPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return mTotalPages;
        }

        @Override
        public Fragment getItem(int i) {
            return PostListPagerFragment.newInstance(mThreadId, i + 1);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            ObjectUtil.cast(object, BaseFragment.class).destroyRetainedFragment();

            super.destroyItem(container, position, object);
        }
    }

    public static class ThreadFavouritesAddDialogFragment extends DialogFragment {

        private static final String TAG = "thread_favourites_add_dialog";

        public static ThreadFavouritesAddDialogFragment newInstance(CharSequence threadId) {
            ThreadFavouritesAddDialogFragment fragment = new ThreadFavouritesAddDialogFragment();

            Bundle bundle = new Bundle();
            bundle.putCharSequence(ARG_THREAD_ID, threadId);
            fragment.setArguments(bundle);

            return fragment;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            View view =
                    getActivity().getLayoutInflater().inflate(
                            R.layout.dialog_favourites_add,
                            (ViewGroup) getActivity().findViewById(R.id.drawer_layout),
                            false);

            AlertDialog alertDialog =
                    new AlertDialog.Builder(getActivity())
                            .setTitle(R.string.dialog_title_favourites_add)
                            .setView(view)
                            .setPositiveButton(android.R.string.ok, null)
                            .setNegativeButton(
                                    android.R.string.cancel, null)
                            .create();

            alertDialog.setOnShowListener(dialog ->
                    alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v ->
                            LoaderDialogFragment.newInstance(
                                    getArguments().getCharSequence(ARG_THREAD_ID),
                                    ((EditText) view.findViewById(R.id.remark)).getText())
                                    .show(getFragmentManager(), LoaderDialogFragment.TAG)));

            return alertDialog;
        }

        public static class LoaderDialogFragment extends cl.monsoon.s1next.fragment.LoaderDialogFragment<ResultWrapper> {

            private static final String TAG = "thread_favourites_add_loader_dialog";

            private static final String ARG_REMARK = "remark";

            private static final String STATUS_ADD_TO_FAVOURITES_SUCCESS = "favorite_do_success";
            private static final String STATUS_ADD_TO_FAVOURITES_REPEAT = "favorite_repeat";

            public static LoaderDialogFragment newInstance(CharSequence threadId, CharSequence description) {
                LoaderDialogFragment fragment = new LoaderDialogFragment();

                Bundle bundle = new Bundle();
                bundle.putCharSequence(ARG_THREAD_ID, threadId);
                bundle.putCharSequence(ARG_REMARK, description);
                fragment.setArguments(bundle);

                return fragment;
            }

            @Override
            protected CharSequence getProgressMessage() {
                return getText(R.string.dialog_progress_message_favourites_add);
            }

            @Override
            protected int getStartLoaderId() {
                if (TextUtils.isEmpty(User.getAuthenticityToken())) {
                    return ID_LOADER_GET_AUTHENTICITY_TOKEN;
                } else {
                    return ID_LOADER_ADD_THREAD_TO_FAVOURITES;
                }
            }

            @Override
            protected RequestBody getRequestBody(int loaderId) {
                if (loaderId == ID_LOADER_ADD_THREAD_TO_FAVOURITES) {
                    return Api.getThreadFavouritesAddBuilder(
                            getArguments().getCharSequence(ARG_THREAD_ID),
                            getArguments().getCharSequence(ARG_REMARK));
                }

                return super.getRequestBody(loaderId);
            }

            @Override
            public Loader<AsyncResult<ResultWrapper>> onCreateLoader(int id, Bundle args) {
                if (id == ID_LOADER_GET_AUTHENTICITY_TOKEN) {
                    return
                            new HttpGetLoader<>(
                                    getActivity(),
                                    Api.URL_AUTHENTICITY_TOKEN_HELPER,
                                    ResultWrapper.class);
                } else if (id == ID_LOADER_ADD_THREAD_TO_FAVOURITES) {
                    return
                            new HttpPostLoader<>(
                                    getActivity(),
                                    Api.URL_THREAD_FAVOURITES_ADD,
                                    ResultWrapper.class,
                                    getRequestBody(id));
                }

                return super.onCreateLoader(id, args);
            }

            @Override
            public void onLoadFinished(Loader<AsyncResult<ResultWrapper>> loader, AsyncResult<ResultWrapper> data) {
                AsyncResult asyncResult = ObjectUtil.cast(data, AsyncResult.class);
                if (asyncResult.exception != null) {
                    AsyncResult.handleException(asyncResult.exception);
                } else {
                    int id = loader.getId();
                    if (id == ID_LOADER_GET_AUTHENTICITY_TOKEN) {
                        getLoaderManager().initLoader(ID_LOADER_ADD_THREAD_TO_FAVOURITES, null, this);

                        return;
                    } else if (id == ID_LOADER_ADD_THREAD_TO_FAVOURITES) {
                        ResultWrapper wrapper = ObjectUtil.cast(asyncResult.data, ResultWrapper.class);
                        Result result = wrapper.getResult();

                        ToastUtil.showByText(result.getMessage(), Toast.LENGTH_SHORT);

                        if (result.getStatus().equals(STATUS_ADD_TO_FAVOURITES_SUCCESS)
                                || result.getStatus().equals(STATUS_ADD_TO_FAVOURITES_REPEAT)) {
                            Fragment fragment =
                                    getFragmentManager()
                                            .findFragmentByTag(ThreadFavouritesAddDialogFragment.TAG);
                            if (fragment != null) {
                                new Handler().post(() ->
                                                ObjectUtil.cast(
                                                        fragment,
                                                        ThreadFavouritesAddDialogFragment.class).dismiss()
                                );
                            }
                        }
                    } else {
                        super.onLoadFinished(loader, data);
                    }
                }

                new Handler().post(this::dismiss);
            }

            @Override
            public void onLoaderReset(Loader<AsyncResult<ResultWrapper>> loader) {

            }
        }
    }

    public static class LoginPromptDialog extends DialogFragment {

        private static final String TAG = "login_prompt_dialog";

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            return
                    new AlertDialog.Builder(getActivity())
                            .setMessage(R.string.dialog_message_login_prompt)
                            .setPositiveButton(R.string.action_login,
                                    (dialog, which) -> {
                                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                                        startActivity(intent);
                                    })
                            .setNegativeButton(
                                    android.R.string.cancel, null)
                            .create();
        }
    }
}
