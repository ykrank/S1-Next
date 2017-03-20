package me.ykrank.s1next.view.adapter;


import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.common.base.Objects;

import java.util.List;

import io.reactivex.functions.Function;
import me.ykrank.s1next.R;
import me.ykrank.s1next.util.L;

public class SimpleSpinnerAdapter<T> extends BaseViewHolderAdapter<T, SimpleSpinnerAdapter.SimpleSpinnerViewHolder> {
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

    static class SimpleSpinnerViewHolder extends BaseViewHolderAdapter.BaseViewHolder {
        private TextView textView;

        SimpleSpinnerViewHolder(View rootView) {
            super(rootView);
            textView = (TextView) rootView;
        }
    }
}
