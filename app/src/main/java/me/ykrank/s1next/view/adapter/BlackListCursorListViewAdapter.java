package me.ykrank.s1next.view.adapter;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import me.ykrank.s1next.R;
import me.ykrank.s1next.data.db.BlackListDbWrapper;
import me.ykrank.s1next.data.db.dbmodel.BlackList;
import me.ykrank.s1next.databinding.ItemBlacklistBinding;
import me.ykrank.s1next.viewmodel.BlackListViewModel;


public final class BlackListCursorListViewAdapter extends CursorAdapter {
    private final LayoutInflater mLayoutInflater;

    public BlackListCursorListViewAdapter(Activity activity) {
        super(activity, null, true);

        mLayoutInflater = activity.getLayoutInflater();
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        ItemBlacklistBinding itemBlacklistBinding = DataBindingUtil.inflate(mLayoutInflater,
                R.layout.item_blacklist, parent, false);
        itemBlacklistBinding.setBlackListViewModel(new BlackListViewModel());
        return itemBlacklistBinding.getRoot();
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ItemBlacklistBinding binding = DataBindingUtil.findBinding(view);
        binding.getBlackListViewModel().blacklist.set(BlackListDbWrapper.getInstance().fromCursor(cursor));
        binding.executePendingBindings();
    }

    @Override
    public BlackList getItem(int position) {
        Cursor cursor = (Cursor) super.getItem(position);
        return BlackListDbWrapper.getInstance().fromCursor(cursor);
    }
}
