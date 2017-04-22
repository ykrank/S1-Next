package me.ykrank.s1next.view.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.List;

import io.reactivex.Observable;
import io.rx_cache2.DynamicKeyGroup;
import io.rx_cache2.EvictDynamicKeyGroup;
import me.ykrank.s1next.data.api.model.Forum;
import me.ykrank.s1next.data.api.model.collection.Threads;
import me.ykrank.s1next.data.api.model.wrapper.ThreadsWrapper;
import me.ykrank.s1next.util.L;
import me.ykrank.s1next.util.RxJavaUtil;
import me.ykrank.s1next.view.adapter.ThreadRecyclerViewAdapter;
import me.ykrank.s1next.viewmodel.LoadingViewModel;

/**
 * A Fragment representing one of the pages of threads.
 * <p>
 * Activity or Fragment containing this must implement
 * {@link PagerCallback} and {@link SubForumsCallback}.
 */
public final class ThreadListPagerFragment extends BaseRecyclerViewFragment<ThreadsWrapper> {

    private static final String ARG_FORUM_ID = "forum_id";
    private static final String ARG_PAGE_NUM = "page_num";

    private String mForumId;
    private int mPageNum;

    private ThreadRecyclerViewAdapter mRecyclerAdapter;

    private PagerCallback mPagerCallback;
    private SubForumsCallback mSubForumsCallback;

    public static ThreadListPagerFragment newInstance(String forumId, int pageNum) {
        ThreadListPagerFragment fragment = new ThreadListPagerFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_FORUM_ID, forumId);
        bundle.putInt(ARG_PAGE_NUM, pageNum);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        mPagerCallback = (PagerCallback) getFragmentManager().findFragmentByTag(
                ThreadListFragment.TAG);
        mSubForumsCallback = (SubForumsCallback) context;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mForumId = getArguments().getString(ARG_FORUM_ID);
        mPageNum = getArguments().getInt(ARG_PAGE_NUM);
        L.leaveMsg("ThreadListPagerFragment##ForumId:" + mForumId + ",PageNum:" + mPageNum);

        RecyclerView recyclerView = getRecyclerView();
        Activity activity = getActivity();
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        mRecyclerAdapter = new ThreadRecyclerViewAdapter(activity, mForumId);
        recyclerView.setAdapter(mRecyclerAdapter);
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mPagerCallback = null;
        mSubForumsCallback = null;
    }

    @Override
    Observable<ThreadsWrapper> getSourceObservable(@LoadingViewModel.LoadingDef int loading) {
        return apiCacheProvider.getThreadsWrapper(mS1Service.getThreadsWrapper(mForumId, mPageNum),
                new DynamicKeyGroup(mForumId + "," + mPageNum, mUser.getKey()), new EvictDynamicKeyGroup(isForceLoading()))
                .compose(RxJavaUtil.jsonTransformer(ThreadsWrapper.class));
    }

    @Override
    void onNext(ThreadsWrapper data) {
        Threads threads = data.getData();
        if (threads.getThreadList().isEmpty()) {
            consumeResult(data.getResult());
        } else {
            super.onNext(data);

            mRecyclerAdapter.diffNewDataSet(threads.getThreadList(), true);

            // update total page
            mPagerCallback.setTotalPageByThreads(threads.getThreadListInfo().getThreads());

            if (!threads.getSubForumList().isEmpty()) {
                mSubForumsCallback.setupSubForums(threads.getSubForumList());
            }
        }
    }

    public interface PagerCallback {

        /**
         * A callback to set actual total pages
         * which used for {@link android.support.v4.view.PagerAdapter}。
         */
        void setTotalPageByThreads(int threads);
    }

    public interface SubForumsCallback {

        void setupSubForums(List<Forum> forumList);
    }
}
