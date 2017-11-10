package me.ykrank.s1next.view.adapter.delegate

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup

import me.ykrank.s1next.data.api.model.HomeThread
import me.ykrank.s1next.databinding.ItemHomeThreadBinding
import me.ykrank.s1next.viewmodel.HomeThreadViewModel

/**
 * Created by ykrank on 2017/2/4.
 */

class HomeThreadAdapterDelegate(context: Context) : BaseAdapterDelegate<HomeThread, HomeThreadAdapterDelegate.BindingViewHolder>(context, HomeThread::class.java) {

    override fun onBindViewHolderData(thread: HomeThread, position: Int, holder: BindingViewHolder, payloads: List<Any>) {
        val binding = holder.binding
        binding.model.thread.set(thread)
        binding.executePendingBindings()
    }

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val binding = ItemHomeThreadBinding.inflate(mLayoutInflater, parent, false)
        binding.model = HomeThreadViewModel()
        return HomeThreadAdapterDelegate.BindingViewHolder(binding)
    }

    class BindingViewHolder(val binding: ItemHomeThreadBinding) : RecyclerView.ViewHolder(binding.root)
}
