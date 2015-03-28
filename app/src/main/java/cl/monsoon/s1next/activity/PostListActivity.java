package cl.monsoon.s1next.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.Loader;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
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

import org.apache.commons.lang3.Range;

import cl.monsoon.s1next.Api;
import cl.monsoon.s1next.R;
import cl.monsoon.s1next.adapter.ThreadAttachmentInfoListArrayAdapter;
import cl.monsoon.s1next.fragment.BaseFragment;
import cl.monsoon.s1next.fragment.LoaderDialogFragment;
import cl.monsoon.s1next.fragment.PostListPagerFragment;
import cl.monsoon.s1next.model.Result;
import cl.monsoon.s1next.model.list.PostList;
import cl.monsoon.s1next.model.mapper.ResultWrapper;
import cl.monsoon.s1next.singleton.Config;
import cl.monsoon.s1next.singleton.MyAccount;
import cl.monsoon.s1next.util.MathUtil;
import cl.monsoon.s1next.util.NetworkUtil;
import cl.monsoon.s1next.util.StringUtil;
import cl.monsoon.s1next.util.ToastUtil;
import cl.monsoon.s1next.widget.AsyncResult;
import cl.monsoon.s1next.widget.FragmentStatePagerAdapter;
import cl.monsoon.s1next.widget.HttpGetLoader;
import cl.monsoon.s1next.widget.HttpPostLoader;
import cl.monsoon.s1next.widget.InputFilterRange;

/**
 * An Activity which includes {@link android.support.v4.view.ViewPager}
 * to represent each page of post lists.
 */
public class PostListActivity extends BaseActivity
        implements PostListPagerFragment.PagerCallback,
        View.OnClickListener {

    public static final String ARG_THREAD = "thread";
    public static final String ARG_SHOULD_GO_TO_LAST_PAGE = "should_go_to_last_page";

    public static final String ACTION_QUOTE = "quote";

    /**
     * The serialization (saved instance state) Bundle key representing
     * the SeekBar's progress when page flip dialog is showing.
     */
    private static final String STATE_SEEKBAR_PROGRESS = "seekbar_progress";
    private int mSeekBarProgress = -1;

    private String mThreadId;

    private String mThreadTitle;
    private int mTotalPages;

    /**
     * The {@link FragmentStatePagerAdapter} will provide
     * fragments for each page of posts.
     */
    private PagerAdapter mAdapter;
    private ViewPager mViewPager;

    private PostList.ThreadAttachment mThreadAttachment;
    private MenuItem mMenuThreadAttachment;

    private MenuItem mMenuPageFlip;

    private BroadcastReceiver mWifiReceiver;

    private BroadcastReceiver mQuoteReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        enableWindowTranslucentStatus();
        setContentView(R.layout.activity_base);

        setNavDrawerIndicatorEnabled(false);

        cl.monsoon.s1next.model.Thread thread = getIntent().getParcelableExtra(ARG_THREAD);
        // we have no title if we use `Jump to thread` feature
        mThreadTitle = thread.getTitle();
        if (TextUtils.isEmpty(mThreadTitle)) {
            // disable drawer and set navigation icon to cross
            // because this activity don't use `singleTop` launch
            // mode here
            setTitle(null);
            setupNavCrossIcon();
            setNavDrawerEnabled(false);
            ((DrawerLayout) findViewById(R.id.drawer_layout))
                    .setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            findViewById(R.id.drawer).setVisibility(View.GONE);
        } else {
            setTitle(mThreadTitle, 1);
        }
        mThreadId = thread.getId();
        // +1 for original post
        setTotalPages(thread.getReplies() + 1);

        FrameLayout container = (FrameLayout) findViewById(R.id.frame_layout);
        View.inflate(this, R.layout.screen_slide, container);

        setupFloatingActionButton(R.drawable.ic_menu_comment_white_24dp);

        mViewPager = (ViewPager) container.findViewById(R.id.pager);
        mAdapter = new PostListPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mAdapter);
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                showOrHideToolbarAndFab(true);
            }

            @Override
            public void onPageSelected(int position) {
                setTitle(mThreadTitle, position + 1);
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

        // Registers broadcast receiver to check whether Wi-Fi is enabled
        // when we need to download images.
        if (Config.needToTurnWifiOn()) {
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
                        intent.getStringExtra(ReplyActivity.ARG_QUOTE_POST_ID),
                        intent.getStringExtra(ReplyActivity.ARG_QUOTE_POST_COUNT));
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

        mMenuThreadAttachment = menu.findItem(R.id.menu_thread_attachment);
        if (mThreadAttachment == null) {
            mMenuThreadAttachment.setVisible(false);
        }

        mMenuPageFlip = menu.findItem(R.id.menu_page_flip);
        prepareMenuPageFlip();

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_thread_attachment:
                showThreadAttachmentDialog();

                return true;
            case R.id.menu_page_flip:
                showPageFlipDialog();

                return true;
            case R.id.menu_favourites_add:
                if (checkUserLoggedInStatus()) {
                    ThreadFavouritesAddDialogFragment.newInstance(mThreadId)
                            .show(getSupportFragmentManager(), ThreadFavouritesAddDialogFragment.TAG);
                }

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt(STATE_SEEKBAR_PROGRESS, mSeekBarProgress);
    }

    public void setupThreadAttachment(PostList.ThreadAttachment threadAttachment) {
        this.mThreadAttachment = threadAttachment;

        // mMenuThreadAttachment = null when configuration changes (like orientation changes)
        // but we don't need to care about the visibility of mMenuThreadAttachment
        // because mThreadAttachment != null and we won't invoke
        // mMenuThreadAttachment.setVisible(false) during onCreateOptionsMenu(Menu)
        if (mMenuThreadAttachment != null) {
            mMenuThreadAttachment.setVisible(true);
        }
    }

    private void setTitle(CharSequence title, int pageNum) {
        if (!TextUtils.isEmpty(title)) {
            setTitle(StringUtil.concatWithTwoSpaces(title, pageNum));
        }
    }

    private void showThreadAttachmentDialog() {
        ThreadAttachmentDialogFragment.newInstance(mThreadAttachment)
                .show(getSupportFragmentManager(), ThreadAttachmentDialogFragment.TAG);
    }

    /**
     * Disables the flip page menu if only has one page.
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
                        R.layout.dialog_page_flip, (ViewGroup) findViewById(R.id.drawer_layout), false);

        if (mSeekBarProgress == -1) {
            mSeekBarProgress = mViewPager.getCurrentItem();
        }

        SeekBar seekbar = (SeekBar) view.findViewById(R.id.seekbar);
        seekbar.setProgress(mSeekBarProgress);
        // SeekBar is zero-based!
        seekbar.setMax(mTotalPages - 1);

        EditText valueView = (EditText) view.findViewById(R.id.value);
        valueView.setText(String.valueOf(mSeekBarProgress + 1));
        valueView.setEms(String.valueOf(mTotalPages).length());
        // set EditText range filter
        valueView.setFilters(
                new InputFilter[]{new InputFilterRange(Range.between(1, mTotalPages))});
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
                    int value = Integer.parseInt(s.toString());
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
                .setTitle(R.string.dialog_title_page_flip)
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
     * Implements {@link PostListPagerFragment.PagerCallback}.
     */
    @Override
    public void setTotalPages(int i) {
        mTotalPages = MathUtil.divide(i, Config.POSTS_PER_PAGE);

        prepareMenuPageFlip();

        if (mAdapter != null) {
            runOnUiThread(mAdapter::notifyDataSetChanged);
        }
    }

    @Override
    public void setThreadTitle(String threadTitle, int pageNum) {
        if (TextUtils.isEmpty(mThreadTitle)) {
            this.mThreadTitle = threadTitle;
            setTitle(threadTitle, pageNum + 1);
        }
    }

    /**
     * {@link com.melnykov.fab.FloatingActionButton#setOnClickListener(android.view.View.OnClickListener)}
     */
    @Override
    public void onClick(View v) {
        startReplyActivity(null, null);
    }

    private void startReplyActivity(String quotePostId, String quotePostCount) {
        if (!checkUserLoggedInStatus()) {
            return;
        }

        Intent intent = new Intent(this, ReplyActivity.class);

        intent.putExtra(ReplyActivity.ARG_THREAD_ID, mThreadId);
        intent.putExtra(ReplyActivity.ARG_THREAD_TITLE, mThreadTitle);

        intent.putExtra(ReplyActivity.ARG_QUOTE_POST_ID, quotePostId);
        intent.putExtra(ReplyActivity.ARG_QUOTE_POST_COUNT, quotePostCount);

        startActivity(intent);
    }

    private boolean checkUserLoggedInStatus() {
        // show LoginPromptDialog if user hasn't logged in.
        if (!MyAccount.hasLoggedIn()) {
            new LoginPromptDialog().show(getSupportFragmentManager(), LoginPromptDialog.TAG);

            return false;
        }

        return true;
    }

    /**
     * Returns a Fragment corresponding to one of the pages of posts.
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
            return PostListPagerFragment.newInstance(mThreadTitle, mThreadId, i + 1);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            ((BaseFragment) object).destroyRetainedFragment();

            super.destroyItem(container, position, object);
        }
    }

    public static class ThreadAttachmentDialogFragment extends DialogFragment {

        private static final String TAG = ThreadAttachmentDialogFragment.class.getSimpleName();

        private static final String ARG_ATTACHMENT_TITLE = "attachment_title";
        private static final String ARG_THREAD_ATTACHMENT_INFO_LIST = "thread_attachment_info_list";

        public static ThreadAttachmentDialogFragment newInstance(PostList.ThreadAttachment threadAttachment) {
            ThreadAttachmentDialogFragment fragment = new ThreadAttachmentDialogFragment();

            Bundle bundle = new Bundle();
            bundle.putString(ARG_ATTACHMENT_TITLE, threadAttachment.getTitle());
            bundle.putParcelableArrayList(
                    ARG_THREAD_ATTACHMENT_INFO_LIST, threadAttachment.getInfoList());
            fragment.setArguments(bundle);

            return fragment;
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return
                    new AlertDialog.Builder(getActivity())
                            .setTitle(getArguments().getString(ARG_ATTACHMENT_TITLE))
                            .setAdapter(
                                    new ThreadAttachmentInfoListArrayAdapter(
                                            getActivity(),
                                            R.layout.two_line_list_item,
                                            getArguments().getParcelableArrayList(
                                                    ARG_THREAD_ATTACHMENT_INFO_LIST)),
                                    null)
                            .setPositiveButton(android.R.string.ok, null)
                            .create();
        }
    }

    public static class ThreadFavouritesAddDialogFragment extends DialogFragment {

        private static final String TAG = ThreadFavouritesAddDialogFragment.class.getSimpleName();

        public static final String ARG_THREAD_ID = "thread_id";

        public static ThreadFavouritesAddDialogFragment newInstance(String threadId) {
            ThreadFavouritesAddDialogFragment fragment = new ThreadFavouritesAddDialogFragment();

            Bundle bundle = new Bundle();
            bundle.putString(ARG_THREAD_ID, threadId);
            fragment.setArguments(bundle);

            return fragment;
        }

        @NonNull
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
                            .setNegativeButton(android.R.string.cancel, null)
                            .create();

            alertDialog.setOnShowListener(dialog ->
                    alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v ->
                            ThreadFavouritesAddLoaderDialogFragment.newInstance(
                                    getArguments().getString(ARG_THREAD_ID),
                                    ((EditText) view.findViewById(R.id.remark)).getText().toString())
                                    .show(getChildFragmentManager(), ThreadFavouritesAddLoaderDialogFragment.TAG)));

            return alertDialog;
        }

        public static class ThreadFavouritesAddLoaderDialogFragment extends LoaderDialogFragment<ResultWrapper> {

            private static final String TAG = ThreadFavouritesAddLoaderDialogFragment.class.getSimpleName();

            private static final String ARG_REMARK = "remark";

            private static final String STATUS_ADD_TO_FAVOURITES_SUCCESS = "favorite_do_success";
            private static final String STATUS_ADD_TO_FAVOURITES_REPEAT = "favorite_repeat";

            public static ThreadFavouritesAddLoaderDialogFragment newInstance(String threadId, String description) {
                ThreadFavouritesAddLoaderDialogFragment fragment =
                        new ThreadFavouritesAddLoaderDialogFragment();

                Bundle bundle = new Bundle();
                bundle.putString(ARG_THREAD_ID, threadId);
                bundle.putString(ARG_REMARK, description);
                fragment.setArguments(bundle);

                return fragment;
            }

            @Override
            protected CharSequence getProgressMessage() {
                return getText(R.string.dialog_progress_message_favourites_add);
            }

            @Override
            protected int getStartLoaderId() {
                if (TextUtils.isEmpty(MyAccount.getAuthenticityToken())) {
                    return ID_LOADER_GET_AUTHENTICITY_TOKEN;
                } else {
                    return ID_LOADER_ADD_THREAD_TO_FAVOURITES;
                }
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
                                    Api.getThreadFavouritesAddBuilder(
                                            getArguments().getString(ARG_THREAD_ID),
                                            getArguments().getString(ARG_REMARK)));
                }

                return super.onCreateLoader(id, args);
            }

            @Override
            public void onLoadFinished(Loader<AsyncResult<ResultWrapper>> loader, AsyncResult<ResultWrapper> asyncResult) {
                if (asyncResult.exception != null) {
                    asyncResult.handleException();
                } else {
                    int id = loader.getId();
                    if (id == ID_LOADER_GET_AUTHENTICITY_TOKEN) {
                        getLoaderManager().initLoader(ID_LOADER_ADD_THREAD_TO_FAVOURITES, null, this);

                        return;
                    } else if (id == ID_LOADER_ADD_THREAD_TO_FAVOURITES) {
                        ResultWrapper wrapper = asyncResult.data;
                        Result result = wrapper.getResult();

                        ToastUtil.showByText(result.getMessage(), Toast.LENGTH_SHORT);

                        if (result.getStatus().equals(STATUS_ADD_TO_FAVOURITES_SUCCESS)
                                || result.getStatus().equals(STATUS_ADD_TO_FAVOURITES_REPEAT)) {
                            new Handler().post(((ThreadFavouritesAddDialogFragment) getParentFragment())::dismiss);
                        }
                    } else {
                        super.onLoadFinished(loader, asyncResult);
                    }
                }

                new Handler().post(this::dismiss);
            }
        }
    }

    public static class LoginPromptDialog extends DialogFragment {

        private static final String TAG = LoginPromptDialog.class.getSimpleName();

        @NonNull
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
                            .setNegativeButton(android.R.string.cancel, null)
                            .create();
        }
    }
}
