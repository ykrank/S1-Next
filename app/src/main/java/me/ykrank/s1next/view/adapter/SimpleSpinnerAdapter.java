package me.ykrank.s1next.view.adapter;


import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.ykrank.androidtools.ui.adapter.simple.SimpleViewHolderAdapter;
import com.github.ykrank.androidtools.util.L;
import com.google.common.base.Objects;

import java.util.List;

import io.reactivex.functions.Function;
import me.ykrank.s1next.R;

public class SimpleSpinnerAdapter<T> extends SimpleViewHolderAdapter<T, SimpleSpinnerAdapter.SimpleSpinnerViewHolder> {
    @NonNull
    private Function<T, String> getName;

    public SimpleSpinnerAdapter(Context context, List<T> objects, @NonNull Function<T, String> getName) {
        super(context, R.layout.spinner_simple, objects);
        this.getName = getName;
    }

    @Override
    @NonNull
    public SimpleSpinnerViewHolder onCreateViewHolder(ViewGroup parent) {
        View root = layoutInflater.inflate(mResource, parent, false);
        return new SimpleSpinnerViewHolder(root);
    }

    @Override
    public void onBindViewHolder(@NonNull SimpleSpinnerViewHolder viewHolder, T data, int position) {
        try {
            viewHolder.textView.setText(getName.apply(data));
        } catch (Exception e) {
            L.report(e);
        }
    }


    @Override
    public long getItemId(int position) {
        return Objects.hashCode(getItem(position));
    }

    static class SimpleSpinnerViewHolder extends SimpleViewHolderAdapter.BaseViewHolder {
        private TextView textView;

        SimpleSpinnerViewHolder(View rootView) {
            super(rootView);
            textView = (TextView) rootView;
        }
    }
}
