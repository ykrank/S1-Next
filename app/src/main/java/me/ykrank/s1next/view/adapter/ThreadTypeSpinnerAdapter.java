package me.ykrank.s1next.view.adapter;


import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import me.ykrank.s1next.R;
import me.ykrank.s1next.data.api.model.ThreadType;

public class ThreadTypeSpinnerAdapter extends BaseViewHolderAdapter<ThreadType, ThreadTypeSpinnerAdapter.ThreadTypeSpinnerViewHolder> {

    public ThreadTypeSpinnerAdapter(Context context, List<ThreadType> objects) {
        super(context, R.layout.spinner_thread_type, objects);
    }

    @Override
    @NonNull
    public ThreadTypeSpinnerViewHolder onCreateViewHolder(ViewGroup parent) {
        View root = layoutInflater.inflate(mResource, parent, false);
        return new ThreadTypeSpinnerViewHolder(root);
    }

    @Override
    public void onBindViewHolder(@NonNull ThreadTypeSpinnerViewHolder viewHolder, ThreadType threadType, int position) {
        viewHolder.textView.setText(threadType.getTypeName());
    }

    @Override
    public long getItemId(int position) {
        return Integer.parseInt(getItem(position).getTypeId());
    }

    final class ThreadTypeSpinnerViewHolder extends BaseViewHolderAdapter.BaseViewHolder {
        private TextView textView;

        ThreadTypeSpinnerViewHolder(View rootView) {
            super(rootView);
            textView = (TextView) rootView;
        }
    }
}
