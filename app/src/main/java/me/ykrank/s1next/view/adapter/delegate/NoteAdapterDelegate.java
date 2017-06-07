package me.ykrank.s1next.view.adapter.delegate;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.List;

import me.ykrank.s1next.data.api.model.Note;
import me.ykrank.s1next.databinding.ItemNoteBinding;
import me.ykrank.s1next.viewmodel.NoteViewModel;

/**
 * Created by ykrank on 2017/1/5.
 */

public class NoteAdapterDelegate extends BaseAdapterDelegate<Note, NoteAdapterDelegate.BindingViewHolder> {

    public NoteAdapterDelegate(Context context) {
        super(context);
    }

    @NonNull
    @Override
    protected Class<Note> getTClass() {
        return Note.class;
    }

    @Override
    public void onBindViewHolderData(Note note, int position, @NonNull BindingViewHolder holder, @NonNull List<Object> payloads) {
        ItemNoteBinding binding = holder.binding;
        binding.getModel().data.set(note);
        binding.executePendingBindings();
    }

    @NonNull
    @Override
    protected RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent) {
        ItemNoteBinding binding = ItemNoteBinding.inflate(mLayoutInflater, parent, false);
        binding.setModel(new NoteViewModel());
        return new BindingViewHolder(binding);
    }

    static final class BindingViewHolder extends RecyclerView.ViewHolder {

        private final ItemNoteBinding binding;

        public BindingViewHolder(ItemNoteBinding binding) {
            super(binding.getRoot());

            this.binding = binding;
        }
    }
}
