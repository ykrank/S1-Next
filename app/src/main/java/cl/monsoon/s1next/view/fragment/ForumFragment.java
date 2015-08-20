package cl.monsoon.s1next.view.fragment;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import cl.monsoon.s1next.R;
import cl.monsoon.s1next.data.api.Api;
import cl.monsoon.s1next.data.api.model.Forum;
import cl.monsoon.s1next.data.api.model.collection.ForumGroups;
import cl.monsoon.s1next.data.api.model.wrapper.ForumGroupsWrapper;
import cl.monsoon.s1next.util.IntentUtil;
import cl.monsoon.s1next.view.activity.ThreadListActivity;
import cl.monsoon.s1next.view.adapter.ForumListRecyclerViewAdapter;
import cl.monsoon.s1next.view.internal.ToolbarDropDownInterface;
import rx.Observable;

/**
 * A Fragment represents forum list.
 */
public final class ForumFragment extends BaseFragment<ForumGroupsWrapper>
        implements ToolbarDropDownInterface.OnItemSelectedListener {

    public static final String TAG = ForumFragment.class.getName();

    private ForumListRecyclerViewAdapter mRecyclerAdapter;
    private ForumGroups mForumGroups;

    private ToolbarDropDownInterface.Callback mToolbarCallback;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = getRecyclerView();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerAdapter = new ForumListRecyclerViewAdapter(getActivity());
        recyclerView.setAdapter(mRecyclerAdapter);

        // the forum list's each element has fixed size
        recyclerView.setHasFixedSize(true);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        mToolbarCallback = (ToolbarDropDownInterface.Callback) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mToolbarCallback = null;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_forum, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_ether:
                Forum forum = new Forum();
                forum.setId("75");
                forum.setName("外野");
                forum.setThreads(10 * Api.THREADS_PER_PAGE);
                ThreadListActivity.startThreadListActivity(getContext(), forum);

                return true;
            case R.id.menu_browser:
                IntentUtil.startViewIntentExcludeOurApp(getContext(), Uri.parse(Api.BASE_URL));

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    Observable<ForumGroupsWrapper> getSourceObservable() {
        return mS1Service.getForumGroupsWrapper();
    }

    @Override
    void onNext(ForumGroupsWrapper data) {
        super.onNext(data);

        mForumGroups = data.getForumGroups();
        // host activity would call #onToolbarDropDownItemSelected(int) after
        mToolbarCallback.setupToolbarDropDown(mForumGroups.getForumGroupNameList());
    }

    /**
     * Show all forums when {@code position == 0} otherwise show
     * corresponding forum group's forum list.
     */
    @Override
    public void onToolbarDropDownItemSelected(int position) {
        if (position == 0) {
            mRecyclerAdapter.setDataSet(mForumGroups.getForumList());
        } else {
            // the first position is "全部"
            // so position - 1 to correspond its group
            mRecyclerAdapter.setDataSet(mForumGroups.getForumGroupList().get(position - 1));
        }
        mRecyclerAdapter.notifyDataSetChanged();
    }
}
