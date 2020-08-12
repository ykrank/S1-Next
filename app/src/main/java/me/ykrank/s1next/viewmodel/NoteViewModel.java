package me.ykrank.s1next.viewmodel;

import android.net.Uri;
import android.text.TextUtils;
import android.view.View;

import androidx.databinding.ObservableField;

import me.ykrank.s1next.data.api.model.Note;
import me.ykrank.s1next.view.page.post.PostListGatewayActivity;


public final class NoteViewModel {

    public final ObservableField<Note> data = new ObservableField<>();

    public View.OnClickListener clickNote() {
        return v -> {
            String url = data.get().getUrl();
            if (!TextUtils.isEmpty(url)) {
                PostListGatewayActivity.Companion.start(v.getContext(), Uri.parse(url));
            }
        };
    }
}
