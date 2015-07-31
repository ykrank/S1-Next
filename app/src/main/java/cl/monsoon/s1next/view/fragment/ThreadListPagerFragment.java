package cl.monsoon.s1next.view.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import java.util.List;

import cl.monsoon.s1next.data.api.model.Forum;
import cl.monsoon.s1next.data.api.model.collection.Threads;
import cl.monsoon.s1next.data.api.model.wrapper.ThreadsWrapper;
import cl.monsoon.s1next.util.ToastUtil;
import cl.monsoon.s1next.view.adapter.ThreadListRecyclerViewAdapter;
import rx.Observable;

/**
 * A Fragment representing one of the pages of threads.
 * <p>
 * Activity or Fragment containing this must implement
 * {@link PagerCallback} and {@link SubForumsCallback}.
 */
public final class ThreadListPagerFragment extends BaseFragment<ThreadsWrapper> {

    private static final String ARG_FORUM_ID = "forum_id";
    private static final String ARG_PAGE_NUM = "page_num";

    private String mForumId;
    private int mPageNum;

    private ThreadListRecyclerViewAdapter mRecyclerAdapter;

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
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        mPagerCallback = (PagerCallback) getFragmentManager().findFragmentByTag(
                ThreadListFragment.TAG);
        mSubForumsCallback = (SubForumsCallback) activity;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mForumId = getArguments().getString(ARG_FORUM_ID);
        mPageNum = getArguments().getInt(ARG_PAGE_NUM);

        RecyclerView recyclerView = getRecyclerView();
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerAdapter = new ThreadListRecyclerViewAdapter(getActivity());
        recyclerView.setAdapter(mRecyclerAdapter);
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mPagerCallback = null;
        mSubForumsCallback = null;
    }

    @Override
    Observable<ThreadsWrapper> getSourceObservable() {
        return mS1Service.getThreadsWrapper(mForumId, mPageNum);
    }

    @Override
    void onNext(ThreadsWrapper data) {
        super.onNext(data);

        Threads threads = data.getThreads();

        if (threads.getThreadList().isEmpty()) {
            String message = data.getResult().getMessage();
            // if user has logged out or has no permission to access this forum
            if (!TextUtils.isEmpty(message)) {
                ToastUtil.showByText(message, Toast.LENGTH_SHORT);
            }
        } else {
            mRecyclerAdapter.setDataSet(threads.getThreadList());
            mRecyclerAdapter.notifyDataSetChanged();

            // update total page
            mPagerCallback.setTotalPageByThreads(threads.getThreadListInfo().getThreads());
        }

        if (!threads.getSubForumList().isEmpty()) {
            mSubForumsCallback.setupSubForums(threads.getSubForumList());
        }
    }

    @Override
    void onError(Throwable throwable) {
        if (getUserVisibleHint()) {
            super.onError(throwable);
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
