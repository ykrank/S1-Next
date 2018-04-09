package me.ykrank.s1next.view.adapter.delegate

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup

import me.ykrank.s1next.data.api.model.Note
import me.ykrank.s1next.databinding.ItemNoteBinding
import me.ykrank.s1next.viewmodel.NoteViewModel

/**
 * Created by ykrank on 2017/1/5.
 */

class NoteAdapterDelegate(context: Context) : BaseAdapterDelegate<Note, NoteAdapterDelegate.BindingViewHolder>(context, Note::class.java) {

    override fun onBindViewHolderData(t: Note, position: Int, holder: BindingViewHolder, payloads: List<Any>) {
        val binding = holder.binding
        binding.model?.data?.set(t)
        binding.executePendingBindings()
    }

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val binding = ItemNoteBinding.inflate(mLayoutInflater, parent, false)
        binding.model = NoteViewModel()
        return BindingViewHolder(binding)
    }

    class BindingViewHolder(val binding: ItemNoteBinding) : RecyclerView.ViewHolder(binding.root)
}
