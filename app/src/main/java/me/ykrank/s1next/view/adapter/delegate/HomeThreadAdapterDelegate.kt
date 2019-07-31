package me.ykrank.s1next.view.adapter.delegate

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.ViewGroup
import com.github.ykrank.androidtools.ui.adapter.simple.SimpleRecycleViewHolder

import me.ykrank.s1next.data.api.model.HomeThread
import me.ykrank.s1next.databinding.ItemHomeThreadBinding
import me.ykrank.s1next.viewmodel.HomeThreadViewModel

/**
 * Created by ykrank on 2017/2/4.
 */

class HomeThreadAdapterDelegate(context: Context) : BaseAdapterDelegate<HomeThread, SimpleRecycleViewHolder<ItemHomeThreadBinding>>(context, HomeThread::class.java) {

    override fun onBindViewHolderData(thread: HomeThread, position: Int, holder: SimpleRecycleViewHolder<ItemHomeThreadBinding>, payloads: List<Any>) {
        val binding = holder.binding
        binding.model?.thread?.set(thread)
    }

    override fun onCreateViewHolder(parent: ViewGroup): androidx.recyclerview.widget.RecyclerView.ViewHolder {
        val binding = ItemHomeThreadBinding.inflate(mLayoutInflater, parent, false)
        binding.model = HomeThreadViewModel()
        return SimpleRecycleViewHolder<ItemHomeThreadBinding>(binding)
    }
}
