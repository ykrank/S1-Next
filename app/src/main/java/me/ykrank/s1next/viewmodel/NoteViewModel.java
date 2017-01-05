package me.ykrank.s1next.viewmodel;

import android.databinding.ObservableField;
import android.view.View;

import me.ykrank.s1next.data.api.model.Note;


public final class NoteViewModel {

    public final ObservableField<Note> data = new ObservableField<>();

    public View.OnClickListener clickNote() {
        return v -> {

        };
    }
}
