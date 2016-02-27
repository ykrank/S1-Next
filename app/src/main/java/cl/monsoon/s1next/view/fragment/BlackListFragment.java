package cl.monsoon.s1next.view.fragment;

import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import cl.monsoon.s1next.R;
import cl.monsoon.s1next.data.db.BlackListDbWrapper;
import cl.monsoon.s1next.databinding.FragmentBlacklistBinding;
import cl.monsoon.s1next.view.adapter.BlackListCursorRecyclerViewAdapter;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * An Activity includes download settings that allow users
 * to modify download features and behaviors such as cache
 * size and avatars/images download strategy.
 */
public final class BlackListFragment extends Fragment {
    public static final String TAG = BlackListFragment.class.getName();

    private static final String ARG_ROW = "row";
    private static final int LIMIT = 20;

    private RecyclerView mRecyclerView;
    private BlackListCursorRecyclerViewAdapter mRecyclerAdapter;

    private Subscription mSubscription;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FragmentBlacklistBinding binding = DataBindingUtil.inflate(inflater,
                R.layout.fragment_blacklist, container, false);
        mRecyclerView = binding.recyclerView;
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerAdapter = new BlackListCursorRecyclerViewAdapter(getActivity());
        mRecyclerView.setAdapter(mRecyclerAdapter);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_blacklist, menu);
        menu.findItem(R.id.menu_refresh).setEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_refresh:
                load();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Starts to load new data.
     */
    private void load() {
        mSubscription = getSourceObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(cursor ->{
                    mRecyclerAdapter.changeCursor(cursor);
                }, throwable -> {
                    Log.e("S1next",throwable.getMessage());
                });
    }

    Observable<Cursor> getSourceObservable() {
        return Observable.create(new Observable.OnSubscribe<Cursor>() {
            @Override
            public void call(Subscriber<? super Cursor> subscriber) {
                Cursor cursor = BlackListDbWrapper.getInstance().getBlackListCursor();
                subscriber.onNext(cursor);
                subscriber.onCompleted();
            }
        });
    }

    @Override
    public void onPause() {
        mRecyclerAdapter.closeCursor();
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        load();
    }

    private ActionMode.Callback actionModeCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.actionmode_blacklist_edit, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {

        }
    };
}
