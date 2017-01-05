package me.ykrank.s1next.view.adapter;

import android.app.Activity;

import me.ykrank.s1next.view.adapter.delegate.FooterProgressAdapterDelegate;
import me.ykrank.s1next.view.adapter.delegate.NoteAdapterDelegate;

public final class NoteRecyclerViewAdapter extends BaseRecyclerViewAdapter {

    public NoteRecyclerViewAdapter(Activity activity) {
        super(activity);

        addAdapterDelegate(new NoteAdapterDelegate(activity));
        addAdapterDelegate(new FooterProgressAdapterDelegate(activity));
    }
}
