package cl.monsoon.s1next.view.adapter;

import android.app.Activity;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import cl.monsoon.s1next.R;
import cl.monsoon.s1next.data.db.BlackListDbWrapper;
import cl.monsoon.s1next.databinding.ItemBlacklistBinding;
import cl.monsoon.s1next.viewmodel.BlackListViewModel;
import cl.monsoon.s1next.widget.CursorRecyclerViewAdapter;


public final class BlackListCursorRecyclerViewAdapter extends CursorRecyclerViewAdapter<BlackListCursorRecyclerViewAdapter.BlackListViewBindingHolder> {
    private final LayoutInflater mLayoutInflater;

    public BlackListCursorRecyclerViewAdapter(Activity activity) {
        super(activity, null);

        mLayoutInflater = activity.getLayoutInflater();
    }

    @Override
    public BlackListViewBindingHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemBlacklistBinding itemBlacklistBinding = DataBindingUtil.inflate(mLayoutInflater,
                R.layout.item_blacklist, parent, false);
        itemBlacklistBinding.setBlackListViewModel(new BlackListViewModel());
        return new BlackListViewBindingHolder(itemBlacklistBinding);
    }

    @Override
    public void onBindViewHolder(BlackListViewBindingHolder viewHolder, Cursor cursor) {
        ItemBlacklistBinding binding = viewHolder.itemBlacklistBinding;
        binding.getBlackListViewModel().blacklist.set(BlackListDbWrapper.getInstance().fromCursor(cursor));
        binding.executePendingBindings();
    }


    static final class BlackListViewBindingHolder extends RecyclerView.ViewHolder {

        private final ItemBlacklistBinding itemBlacklistBinding;

        public BlackListViewBindingHolder(ItemBlacklistBinding itemBlacklistBinding) {
            super(itemBlacklistBinding.getRoot());

            this.itemBlacklistBinding = itemBlacklistBinding;
        }
    }
}
