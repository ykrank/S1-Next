package me.ykrank.s1next.viewmodel;

import android.databinding.ObservableField;
import android.net.Uri;
import android.view.View;

import me.ykrank.s1next.data.api.model.Note;
import me.ykrank.s1next.view.activity.PostListGatewayActivity;


public final class NoteViewModel {

    public final ObservableField<Note> data = new ObservableField<>();

    public View.OnClickListener clickNote() {
        return v -> {
            PostListGatewayActivity.start(v.getContext(), Uri.parse(data.get().getUrl()));
        };
    }
}
