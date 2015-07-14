package cl.monsoon.s1next.view.activity;

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
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.Loader;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.squareup.otto.Subscribe;

import javax.inject.Inject;

import cl.monsoon.s1next.Api;
import cl.monsoon.s1next.App;
import cl.monsoon.s1next.Config;
import cl.monsoon.s1next.R;
import cl.monsoon.s1next.data.Wifi;
import cl.monsoon.s1next.data.api.model.Result;
import cl.monsoon.s1next.data.api.model.list.Posts;
import cl.monsoon.s1next.data.api.model.mapper.ResultWrapper;
import cl.monsoon.s1next.data.pref.DownloadPreferencesManager;
import cl.monsoon.s1next.event.QuoteEvent;
import cl.monsoon.s1next.singleton.BusProvider;
import cl.monsoon.s1next.util.MathUtil;
import cl.monsoon.s1next.util.NetworkUtil;
import cl.monsoon.s1next.util.StringUtil;
import cl.monsoon.s1next.util.ToastUtil;
import cl.monsoon.s1next.view.adapter.ThreadAttachmentInfoListArrayAdapter;
import cl.monsoon.s1next.view.fragment.BaseFragment;
import cl.monsoon.s1next.view.fragment.LoaderDialogFragment;
import cl.monsoon.s1next.view.fragment.PageTurningDialogFragment;
import cl.monsoon.s1next.view.fragment.PostListPagerFragment;
import cl.monsoon.s1next.widget.AsyncResult;
import cl.monsoon.s1next.widget.FragmentStatePagerAdapter;
import cl.monsoon.s1next.widget.HttpGetLoader;
import cl.monsoon.s1next.widget.HttpPostLoader;

/**
 * An Activity which includes {@link android.support.v4.view.ViewPager}
 * to represent each page of post lists.
 */
public final class PostListActivity extends BaseActivity
        implements PostListPagerFragment.PagerCallback,
        View.OnClickListener {

    public static final String ARG_THREAD = "thread";
    public static final String ARG_QUOTE_POST_ID = "quote_post_id";

    /**
     * ARG_JUMP_PAGE takes precedence over {@link #ARG_SHOULD_GO_TO_LAST_PAGE}.
     */
    public static final String ARG_JUMP_PAGE = "jump_page";
    public static final String ARG_SHOULD_GO_TO_LAST_PAGE = "should_go_to_last_page";

    @Inject
    DownloadPreferencesManager mDownloadPreferencesManager;

    @Inject
    Wifi mWifi;

    private String mThreadId;

    private String mThreadTitle;
    private int mTotalPages;

    /**
     * The {@link FragmentStatePagerAdapter} will provide
     * fragments for each page of posts.
     */
    private PagerAdapter mAdapter;
    private ViewPager mViewPager;

    private Posts.ThreadAttachment mThreadAttachment;
    private MenuItem mMenuThreadAttachment;

    private MenuItem mMenuPageTurning;

    private BroadcastReceiver mWifiReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        App.getAppComponent(this).inject(this);

        setNavDrawerIndicatorEnabled(false);

        cl.monsoon.s1next.data.api.model.Thread thread = getIntent().getParcelableExtra(ARG_THREAD);
        mThreadTitle = thread.getTitle();
        mThreadId = thread.getId();

        final int jumpPage = getIntent().getIntExtra(ARG_JUMP_PAGE, -1);
        if (jumpPage != -1) {
            // we do not know the total page if we open this thread by URL
            // so we set the jump page to total page
            setTotalPage(jumpPage);
        } else {
            // +1 for original post
            setTotalPageByPosts(thread.getReplies() + 1);
        }

        FrameLayout container = (FrameLayout) findViewById(R.id.frame_layout);
        View.inflate(this, R.layout.screen_slide, container);

        setupFloatingActionButton(R.drawable.ic_menu_comment_white_24dp);

        mViewPager = (ViewPager) container.findViewById(R.id.view_pager);
        mAdapter = new PostListPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                updateTitleWithPage();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        updateTitleWithPage();

        // do not jump to the first page, because this is the default behavior for ViewPager
        if (jumpPage != -1) {
            mViewPager.setCurrentItem(jumpPage - 1);
        } else if (getIntent().getBooleanExtra(ARG_SHOULD_GO_TO_LAST_PAGE, false)) {
            mViewPager.setCurrentItem(mTotalPages - 1);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Registers broadcast receiver to check whether Wi-Fi is enabled
        // when we need to download images.
        if (mDownloadPreferencesManager.shouldMonitorWifi()) {
            mWifi.setWifiEnabled(NetworkUtil.isWifiConnected());

            mWifiReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    mWifi.setWifiEnabled(NetworkUtil.isWifiConnected());
                }
            };

            IntentFilter intentFilter = new IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION);
            registerReceiver(mWifiReceiver, intentFilter);
        }

        BusProvider.get().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mWifiReceiver != null) {
            unregisterReceiver(mWifiReceiver);
            mWifiReceiver = null;
        }

        BusProvider.get().unregister(this);
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void quote(QuoteEvent event) {
        startReplyActivity(event.getQuotePostId(), event.getQuotePostCount());
    }

    private void updateTitleWithPage() {
        if (!TextUtils.isEmpty(mThreadTitle)) {
            setTitle(StringUtil.concatWithTwoSpaces(mThreadTitle, mViewPager.getCurrentItem() + 1));
        } else {
            setTitle(null);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_post, menu);

        mMenuThreadAttachment = menu.findItem(R.id.menu_thread_attachment);
        if (mThreadAttachment == null) {
            mMenuThreadAttachment.setVisible(false);
        }

        mMenuPageTurning = menu.findItem(R.id.menu_page_turning);
        preparePageTurningMenu();

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (getIntent().getBooleanExtra(PostListGatewayActivity.ARG_COME_FROM_OTHER_APP, false)) {
                    // this activity is not part of this app's task
                    // so create a new task when navigating up
                    TaskStackBuilder.create(this)
                            .addNextIntentWithParentStack(new Intent(this, ForumActivity.class))
                            .startActivities();

                    finish();

                    return true;
                }

                break;
            case R.id.menu_thread_attachment:
                ThreadAttachmentDialogFragment.newInstance(mThreadAttachment).show(
                        getSupportFragmentManager(), ThreadAttachmentDialogFragment.TAG);

                return true;
            case R.id.menu_page_turning:
                new PostListPageTurningDialogFragment(mViewPager.getCurrentItem(), mTotalPages).show(
                        getSupportFragmentManager(), PageTurningDialogFragment.TAG);

                return true;
            case R.id.menu_favourites_add:
                if (checkUserLoggedInStatus()) {
                    ThreadFavouritesAddDialogFragment.newInstance(mThreadId).show(
                            getSupportFragmentManager(), ThreadFavouritesAddDialogFragment.TAG);
                }

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setupThreadAttachment(Posts.ThreadAttachment threadAttachment) {
        this.mThreadAttachment = threadAttachment;

        // mMenuThreadAttachment = null when configuration changes (like orientation changes)
        // but we don't need to care about the visibility of mMenuThreadAttachment
        // because mThreadAttachment != null and we won't invoke
        // mMenuThreadAttachment.setVisible(false) during onCreateOptionsMenu(Menu)
        if (mMenuThreadAttachment != null) {
            mMenuThreadAttachment.setVisible(true);
        }
    }

    /**
     * Disables the page turning menu if only has one page.
     */
    private void preparePageTurningMenu() {
        if (mMenuPageTurning == null) {
            return;
        }

        if (mTotalPages == 1) {
            mMenuPageTurning.setEnabled(false);
        } else {
            mMenuPageTurning.setEnabled(true);
        }
    }

    @Override
    public int getTotalPages() {
        return mTotalPages;
    }

    /**
     * Implements {@link PostListPagerFragment.PagerCallback}.
     */
    @Override
    public void setTotalPageByPosts(int posts) {
        setTotalPage(MathUtil.divide(posts, Config.POSTS_PER_PAGE));
    }

    private void setTotalPage(int totalPage) {
        this.mTotalPages = totalPage;

        preparePageTurningMenu();

        if (mAdapter != null) {
            runOnUiThread(mAdapter::notifyDataSetChanged);
        }
    }

    @Override
    public CharSequence getThreadTitle() {
        return mThreadTitle;
    }

    @Override
    public void setThreadTitle(CharSequence title) {
        mThreadTitle = title.toString();
        updateTitleWithPage();
    }

    /**
     * {@link android.support.design.widget.FloatingActionButton#setOnClickListener(android.view.View.OnClickListener)}
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
            int jumpPage = getIntent().getIntExtra(ARG_JUMP_PAGE, -1);
            String quotePostId = getIntent().getStringExtra(ARG_QUOTE_POST_ID);
            if (jumpPage - 1 == i && !TextUtils.isEmpty(quotePostId)) {
                // clear this extra string because we only need to tell PostListPagerFragment once
                getIntent().putExtra(ARG_QUOTE_POST_ID, (String) null);
                return PostListPagerFragment.newInstance(mThreadId, i + 1, quotePostId);
            } else {
                return PostListPagerFragment.newInstance(mThreadId, i + 1);
            }
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

        public static ThreadAttachmentDialogFragment newInstance(Posts.ThreadAttachment threadAttachment) {
            ThreadAttachmentDialogFragment fragment = new ThreadAttachmentDialogFragment();

            Bundle bundle = new Bundle();
            bundle.putString(ARG_ATTACHMENT_TITLE, threadAttachment.getTitle());
            bundle.putParcelableArrayList(ARG_THREAD_ATTACHMENT_INFO_LIST,
                    threadAttachment.getInfoList());
            fragment.setArguments(bundle);

            return fragment;
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return new AlertDialog.Builder(getActivity())
                    .setTitle(getArguments().getString(ARG_ATTACHMENT_TITLE))
                    .setAdapter(new ThreadAttachmentInfoListArrayAdapter(
                                    getActivity(), R.layout.item_two_line_text,
                                    getArguments().getParcelableArrayList(
                                            ARG_THREAD_ATTACHMENT_INFO_LIST)),
                            null)
                    .setPositiveButton(R.string.dialog_button_text_done, null)
                    .create();
        }
    }

    public static class PostListPageTurningDialogFragment extends PageTurningDialogFragment {
        public PostListPageTurningDialogFragment() {
            // Every fragment must have an empty constructor, so it
            // can be instantiated when restoring its activity's state.
        }

        @SuppressWarnings("ValidFragment")
        public PostListPageTurningDialogFragment(int currentPage, int totalPages) {
            super(currentPage, totalPages);
        }

        @Override
        protected void onPageTurning(int page) {
            ((PostListActivity) getActivity()).mViewPager.setCurrentItem(page);
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
            View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_favourites_add,
                    (ViewGroup) getActivity().findViewById(R.id.drawer_layout), false);

            AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.dialog_title_favourites_add)
                    .setView(view)
                    .setPositiveButton(R.string.dialog_button_text_add, null)
                    .setNegativeButton(android.R.string.cancel, null)
                    .create();

            alertDialog.setOnShowListener(dialog ->
                    alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v ->
                            ThreadFavouritesAddLoaderDialogFragment.newInstance(
                                    getArguments().getString(ARG_THREAD_ID),
                                    ((EditText) view.findViewById(R.id.remark)).getText().toString())
                                    .show(getChildFragmentManager(),
                                            ThreadFavouritesAddLoaderDialogFragment.TAG)));

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
            @LoaderId
            protected int getStartLoaderId() {
                if (TextUtils.isEmpty(App.getAppComponent(getActivity()).getUser()
                        .getAuthenticityToken())) {
                    return ID_LOADER_GET_AUTHENTICITY_TOKEN;
                } else {
                    return ID_LOADER_ADD_THREAD_TO_FAVOURITES;
                }
            }

            @Override
            public Loader<AsyncResult<ResultWrapper>> onCreateLoader(@LoaderId int id, Bundle args) {
                if (id == ID_LOADER_GET_AUTHENTICITY_TOKEN) {
                    return new HttpGetLoader<>(getActivity(), Api.URL_AUTHENTICITY_TOKEN_HELPER,
                            ResultWrapper.class);
                } else if (id == ID_LOADER_ADD_THREAD_TO_FAVOURITES) {
                    return new HttpPostLoader<>(getActivity(), Api.URL_THREAD_FAVOURITES_ADD,
                            ResultWrapper.class, Api.getThreadFavouritesAddBuilder(
                            getArguments().getString(ARG_THREAD_ID),
                            getArguments().getString(ARG_REMARK)));
                }

                return super.onCreateLoader(id, args);
            }

            @Override
            public void onLoadFinished(Loader<AsyncResult<ResultWrapper>> loader, AsyncResult<ResultWrapper> asyncResult) {
                if (asyncResult.exception != null) {
                    ToastUtil.showByResId(asyncResult.getExceptionStringRes(), Toast.LENGTH_SHORT);
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
}
