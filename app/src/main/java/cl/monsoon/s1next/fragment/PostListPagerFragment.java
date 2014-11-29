package cl.monsoon.s1next.fragment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import cl.monsoon.s1next.Api;
import cl.monsoon.s1next.R;
import cl.monsoon.s1next.activity.ReplyActivity;
import cl.monsoon.s1next.adapter.PostListRecyclerAdapter;
import cl.monsoon.s1next.model.list.PostList;
import cl.monsoon.s1next.model.mapper.PostListWrapper;
import cl.monsoon.s1next.singleton.User;
import cl.monsoon.s1next.util.ToastHelper;
import cl.monsoon.s1next.widget.AsyncResult;

/**
 * A Fragment representing one of the pages of posts.
 * All activities containing this Fragment must
 * implement {@link cl.monsoon.s1next.fragment.PostListPagerFragment.OnPagerInteractionCallback}.
 * Similar to {@see ThreadListPagerFragment}
 */
public final class PostListPagerFragment extends BaseFragment<PostListWrapper> {

    private static final String ARG_THREAD_ID = "thread_id";
    private static final String ARG_PAGE_NUM = "page_num";

    private CharSequence mThreadId;
    private int mPageNum;

    private PostListRecyclerAdapter mRecyclerAdapter;

    private MenuItem mMenuReply;

    private OnPagerInteractionCallback mOnPagerInteractionCallback;

    public static PostListPagerFragment newInstance(CharSequence threadId, int page) {
        PostListPagerFragment fragment = new PostListPagerFragment();

        Bundle args = new Bundle();
        args.putCharSequence(ARG_THREAD_ID, threadId);
        args.putInt(ARG_PAGE_NUM, page);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mThreadId = getArguments().getCharSequence(ARG_THREAD_ID);
        mPageNum = getArguments().getInt(ARG_PAGE_NUM);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recycler_view, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerAdapter = new PostListRecyclerAdapter(getActivity());
        mRecyclerView.setAdapter(mRecyclerAdapter);

        int padding = getResources().getDimensionPixelSize(R.dimen.recycler_view_card_padding);
        mRecyclerView.setPadding(0, padding, 0, padding);
    }

    @Override
    public void onResume() {
        super.onResume();

        prepareMenuReply();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (activity instanceof OnPagerInteractionCallback) {
            mOnPagerInteractionCallback = ((OnPagerInteractionCallback) getActivity());
        } else {
            throw new ClassCastException(
                    getActivity()
                            + " must implement OnPagerInteractionListener.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mOnPagerInteractionCallback = null;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.fragment_post, menu);

        // disabled default in fragment_post.xml
        // we will enable it when finish loading
        // post list and user already has logged in
        // see PostListPagerFragment#onPostExecute()
        mMenuReply = menu.findItem(R.id.menu_reply);
        prepareMenuReply();
    }

    /**
     * Sets whether the menu reply is enabled depends on whether user logged before.
     */
    private void prepareMenuReply() {
        if (mMenuReply == null) {
            return;
        }

        if (mRecyclerAdapter.getItemCount() == 0 || TextUtils.isEmpty(User.getName())) {
            mMenuReply.setEnabled(false);
        } else {
            mMenuReply.setEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.menu_browser:
                String url = Api.getUrlBrowserPostList(mThreadId, mPageNum);

                intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));

                startActivity(intent);

                return true;
            case R.id.menu_reply:
                intent = new Intent(getActivity(), ReplyActivity.class);

                intent.putExtra(ReplyActivity.ARG_THREAD_TITLE, getThreadTitle());
                intent.putExtra(ReplyActivity.ARG_THREAD_ID, mThreadId);

                startActivity(intent);

                return true;
            case R.id.menu_share:
                String value =
                        getThreadTitle() + "  " + Api.getUrlBrowserPostList(mThreadId, 1);

                intent = new Intent(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_TEXT, value);
                intent.setType("text/plain");

                startActivity(intent);

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private CharSequence getThreadTitle() {
        CharSequence title = getActivity().getTitle();
        // remove two space and page number's length

        return title.subSequence(0, title.length() - 2 - String.valueOf(mPageNum).length());
    }

    @Override
    public void onRefresh() {
        execute(Api.getUrlPostList(mThreadId, mPageNum), PostListWrapper.class);
    }

    @Override
    public void onPostExecute(AsyncResult<PostListWrapper> asyncResult) {
        super.onPostExecute(asyncResult);

        if (asyncResult.exception != null) {
            if (isVisible()) {
                AsyncResult.handleException(asyncResult.exception);
            }
        } else {
            try {
                PostList postList = asyncResult.data.unwrap();
                mRecyclerAdapter.setDataSet(postList.getPostList());
                mRecyclerAdapter.notifyDataSetChanged();

                prepareMenuReply();

                mOnPagerInteractionCallback.setCount(postList.getPostListInfo().getReplies() + 1);
            } catch (NullPointerException e) {
                ToastHelper.showByResId(R.string.message_server_error);
            }
        }
    }

    /**
     * A callback interface that all activities containing this Fragment must implement.
     */
    public static interface OnPagerInteractionCallback {

        /**
         * Callback to set actual page which used for {@link android.support.v4.view.PagerAdapter}
         */
        public void setCount(int i);
    }
}
