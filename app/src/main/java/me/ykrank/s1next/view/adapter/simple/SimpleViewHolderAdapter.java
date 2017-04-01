package me.ykrank.s1next.view.adapter.simple;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.List;

/**
 * Created by ykrank on 2016/8/1 0001.
 */
public abstract class SimpleViewHolderAdapter<D, VH extends SimpleViewHolderAdapter.BaseViewHolder> extends ArrayAdapter<D> {

    protected LayoutInflater layoutInflater;
    protected
    @LayoutRes
    int mResource;

    public SimpleViewHolderAdapter(Context context, @LayoutRes int resource, List<D> objects) {
        super(context, resource, objects);
        layoutInflater = LayoutInflater.from(context);
        this.mResource = resource;
    }

    @Override
    @SuppressWarnings("unchecked")
    public View getView(int position, View convertView, ViewGroup parent) {
        VH viewHolder;
        if (convertView == null) {
            viewHolder = onCreateViewHolder(parent);
            convertView = viewHolder.getRoot();
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (VH) convertView.getTag();
        }
        onBindViewHolder(viewHolder, getItem(position), position);
        return convertView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getView(position, convertView, parent);
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).hashCode();
    }

    @NonNull
    public abstract VH onCreateViewHolder(ViewGroup parent);

    public abstract void onBindViewHolder(@NonNull VH viewHolder, D data, int position);

    public static abstract class BaseViewHolder {
        private View rootView;

        public BaseViewHolder(View rootView) {
            this.rootView = rootView;
        }

        public View getRoot() {
            return rootView;
        }
    }
}
