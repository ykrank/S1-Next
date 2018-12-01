package me.ykrank.s1next.view.adapter.delegate

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.github.ykrank.androidtools.ui.adapter.simple.SimpleRecycleViewHolder

import me.ykrank.s1next.data.api.model.Note
import me.ykrank.s1next.databinding.ItemNoteBinding
import me.ykrank.s1next.viewmodel.NoteViewModel

/**
 * Created by ykrank on 2017/1/5.
 */

class NoteAdapterDelegate(context: Context) : BaseAdapterDelegate<Note, SimpleRecycleViewHolder<ItemNoteBinding>>(context, Note::class.java) {

    override fun onBindViewHolderData(t: Note, position: Int, holder: SimpleRecycleViewHolder<ItemNoteBinding>, payloads: List<Any>) {
        val binding = holder.binding
        binding.model?.data?.set(t)
    }

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val binding = ItemNoteBinding.inflate(mLayoutInflater, parent, false)
        binding.model = NoteViewModel()
        return SimpleRecycleViewHolder<ItemNoteBinding>(binding)
    }

}
