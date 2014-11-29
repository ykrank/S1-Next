package cl.monsoon.s1next.activity;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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

import cl.monsoon.s1next.R;
import cl.monsoon.s1next.fragment.BaseFragment;
import cl.monsoon.s1next.fragment.PostListPagerFragment;
import cl.monsoon.s1next.singleton.Config;
import cl.monsoon.s1next.util.MathUtil;
import cl.monsoon.s1next.util.NetworkUtil;
import cl.monsoon.s1next.widget.FragmentStatePagerAdapter;
import cl.monsoon.s1next.widget.InputFilterRange;

/**
 * An Activity representing a list of posts.
 * Similar to {@see ThreadListActivity}
 */
public final class PostListActivity
        extends BaseActivity
        implements PostListPagerFragment.OnPagerInteractionCallback {

    public final static String ARG_THREAD_TITLE = "thread_title";
    public final static String ARG_THREAD_ID = "thread_id";
    public final static String ARG_POST_REPLIES = "post_replies";

    /**
     * The serialization (saved instance state) Bundle key representing
     * SeekBar's progress when page flip dialog is showing.
     */
    private static final String STATE_SEEKBAR_PROGRESS = "seekbar_progress";
    private int mSeekBarProgress = -1;

    private CharSequence mThreadId;
    private CharSequence mThreadTitle;
    private int mNumPages;

    /**
     * The {@link FragmentStatePagerAdapter} will provide
     * fragments for each page of posts.
     */
    private PagerAdapter mAdapter;
    private ViewPager mViewPager;

    private MenuItem mMenuPageFlip;

    private BroadcastReceiver wifiReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        setNavDrawerIndicatorEnabled(false);

        // not works well
        //        // Title has marquee effect if thread's title is long.
        //        // Similar use to AbsNavigationDrawerActivity#setupGlobalToolbar()
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
        setTitle(mThreadTitle);
        mThreadId = getIntent().getCharSequenceExtra(ARG_THREAD_ID);
        setCount(getIntent().getIntExtra(ARG_POST_REPLIES, 1));

        FrameLayout container = (FrameLayout) findViewById(R.id.frame_layout);
        View.inflate(this, R.layout.activity_screen_slide, container);

        mViewPager = (ViewPager) container.findViewById(R.id.pager);
        mAdapter = new PostListPagerAdapter(getFragmentManager());
        mViewPager.setAdapter(mAdapter);

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

            wifiReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    Config.setWifi(NetworkUtil.isWifiConnected());
                }
            };

            IntentFilter intentFilter =
                    new IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION);
            registerReceiver(wifiReceiver, intentFilter);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (wifiReceiver != null) {
            unregisterReceiver(wifiReceiver);
            wifiReceiver = null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_post, menu);

        mMenuPageFlip = menu.findItem(R.id.menu_page_flip);
        prepareMenuPageFlip();

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
                showPageFlipDialog();

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
     * Implement {@link cl.monsoon.s1next.fragment.PostListPagerFragment.OnPagerInteractionCallback}.
     */
    @Override
    public void setCount(int i) {
        mNumPages = MathUtil.divide(i, Config.POSTS_PER_PAGE);

        prepareMenuPageFlip();

        if (mAdapter != null) {
            runOnUiThread(mAdapter::notifyDataSetChanged);
        }
    }

    /**
     * Disable flip page menu when {@link cl.monsoon.s1next.activity.PostListActivity#mNumPages} = 1.
     */
    private void prepareMenuPageFlip() {
        if (mMenuPageFlip == null) {
            return;
        }

        if (mNumPages == 1) {
            mMenuPageFlip.setEnabled(false);
        } else {
            mMenuPageFlip.setEnabled(true);
        }
    }

    private void showPageFlipDialog() {
        View view =
                getLayoutInflater().inflate(
                        R.layout.dialog_seekbar, (ViewGroup) findViewById(R.id.root), false);

        if (mSeekBarProgress == -1) {
            mSeekBarProgress = mViewPager.getCurrentItem();
        }

        SeekBar seekbar = (SeekBar) view.findViewById(R.id.seekbar);
        seekbar.setProgress(mSeekBarProgress);
        // seekBar is zero-based!
        seekbar.setMax(mNumPages - 1);

        EditText valueView = (EditText) view.findViewById(R.id.value);
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

    /**
     * Return a Fragment corresponding to one of the pages of posts.
     */
    private class PostListPagerAdapter extends FragmentStatePagerAdapter {

        private PostListPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return mNumPages;
        }

        @Override
        public Fragment getItem(int i) {
            return PostListPagerFragment.newInstance(mThreadId, i + 1);
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            if (!isNavDrawerOpened()) {
                // TODO: We can't see thread page number sometimes because title is long,
                // so it's better to put a TextView in ToolBar to show thread page number
                // or make the title marquee.
                setTitle(mThreadTitle + "  " + (position + 1));
            }

            super.setPrimaryItem(container, position, object);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            if (object instanceof BaseFragment) {
                ((BaseFragment) object).destroyRetainedFragment();
            }

            super.destroyItem(container, position, object);
        }
    }
}
