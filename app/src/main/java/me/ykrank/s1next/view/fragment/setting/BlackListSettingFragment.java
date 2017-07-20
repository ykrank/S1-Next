package me.ykrank.s1next.view.fragment.setting;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;

import com.github.ykrank.androidautodispose.AndroidRxDispose;
import com.github.ykrank.androidlifecycle.event.FragmentEvent;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Single;
import me.ykrank.s1next.R;
import me.ykrank.s1next.data.db.BlackListDbWrapper;
import me.ykrank.s1next.data.db.dbmodel.BlackList;
import me.ykrank.s1next.databinding.FragmentBlacklistBinding;
import me.ykrank.s1next.util.L;
import me.ykrank.s1next.util.RxJavaUtil;
import me.ykrank.s1next.view.adapter.BlackListCursorListViewAdapter;
import me.ykrank.s1next.view.dialog.BlacklistDialogFragment;
import me.ykrank.s1next.view.fragment.BaseFragment;

public final class BlackListSettingFragment extends BaseFragment {
    public static final String TAG = BlackListSettingFragment.class.getName();

    private ListView mListView;
    private BlackListCursorListViewAdapter mListViewAdapter;

    private AbsListView.MultiChoiceModeListener mActionModeCallback = new AbsListView.MultiChoiceModeListener() {

        @Override
        public void onItemCheckedStateChanged(android.view.ActionMode mode, int position, long id, boolean checked) {

        }

        // Called when the action mode is created; startActionMode() was called
        @Override
        public boolean onCreateActionMode(android.view.ActionMode mode, Menu menu) {
            // Inflate a menu resource providing context menu items
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.actionmode_blacklist_edit, menu);
            return true;
        }

        // Called each time the action mode is shown. Always called after onCreateActionMode, but
        // may be called multiple times if the mode is invalidated.
        @Override
        public boolean onPrepareActionMode(android.view.ActionMode mode, Menu menu) {
            return false; // Return false if nothing is done
        }

        // Called when the user selects a contextual menu item
        @Override
        public boolean onActionItemClicked(android.view.ActionMode mode, MenuItem item) {
            SparseBooleanArray checklist = mListView.getCheckedItemPositions();
            switch (item.getItemId()) {
                case R.id.menu_add:
                    add();
                    return true;
                case R.id.menu_edit:
                    BlackList blackList = null;
                    for (int i = 0; i < checklist.size(); i++) {
                        if (checklist.valueAt(i)) {
                            blackList = mListViewAdapter.getItem(checklist.keyAt(i));
                            break;
                        }
                    }
                    BlacklistDialogFragment dialogFragment1 = BlacklistDialogFragment.newInstance(blackList);
                    dialogFragment1.setTargetFragment(BlackListSettingFragment.this, BlacklistDialogFragment.DIALOG_REQUEST_CODE);
                    dialogFragment1.show(getFragmentManager(), BlackListSettingFragment.class.getName());
                    return true;
                case R.id.menu_delete:
                    List<BlackList> blackLists = new ArrayList<>();
                    for (int i = 0; i < checklist.size(); i++) {
                        if (checklist.valueAt(i)) {
                            blackLists.add(mListViewAdapter.getItem(checklist.keyAt(i)));
                        }
                    }
                    BlackListDbWrapper.getInstance().delBlackLists(blackLists);
                    load();
                    return true;
                case R.id.menu_all:
                    for (int i = 0; i < mListView.getCount(); i++) {
                        mListView.setItemChecked(i, true);
                    }
                    return true;
                case R.id.menu_clear:
                    for (int i = 0; i < mListView.getCount(); i++) {
                        mListView.setItemChecked(i, false);
                    }
                    return true;
                default:
                    return false;
            }
        }

        // Called when the user exits the action mode
        @Override
        public void onDestroyActionMode(android.view.ActionMode mode) {

        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FragmentBlacklistBinding binding = DataBindingUtil.inflate(inflater,
                R.layout.fragment_blacklist, container, false);
        mListView = binding.listview;
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mListViewAdapter = new BlackListCursorListViewAdapter(getActivity());
        mListView.setAdapter(mListViewAdapter);
        mListView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);
        mListView.setMultiChoiceModeListener(mActionModeCallback);
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
            case R.id.menu_add:
                add();
                return true;
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
        getSourceObservable()
                .compose(RxJavaUtil.iOSingleTransformer())
                .to(AndroidRxDispose.withSingle(this, FragmentEvent.DESTROY))
                .subscribe(mListViewAdapter::changeCursor,
                        throwable -> L.e("S1next", throwable));
    }

    Single<Cursor> getSourceObservable() {
        return BlackListDbWrapper.getInstance().getBlackListCursor();
    }

    @Override
    public void onPause() {
        mListViewAdapter.changeCursor(null);
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        load();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == BlacklistDialogFragment.DIALOG_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                BlackList blackList = data.getParcelableExtra(BlacklistDialogFragment.BLACKLIST_TAG);
                if (blackList != null) {
                    BlackListDbWrapper.getInstance().saveBlackList(blackList);
                    load();
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void add() {
        BlacklistDialogFragment dialogFragment = BlacklistDialogFragment.newInstance(null);
        dialogFragment.setTargetFragment(BlackListSettingFragment.this, BlacklistDialogFragment.DIALOG_REQUEST_CODE);
        dialogFragment.show(getFragmentManager(), BlackListSettingFragment.class.getName());
    }
}
