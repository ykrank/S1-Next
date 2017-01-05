package me.ykrank.s1next.viewmodel;

import android.content.Intent;
import android.databinding.ObservableField;
import android.net.Uri;
import android.view.View;

import me.ykrank.s1next.data.api.Api;
import me.ykrank.s1next.data.api.model.Note;
import me.ykrank.s1next.view.activity.PostListGatewayActivity;


public final class NoteViewModel {

    public final ObservableField<Note> data = new ObservableField<>();

    public View.OnClickListener clickNote() {
        return v -> {
            Intent intent = new Intent(v.getContext(), PostListGatewayActivity.class);
            intent.setData(Uri.parse(Api.BASE_URL + data.get().getUrl()));
            v.getContext().startActivity(intent);
        };
    }
}
