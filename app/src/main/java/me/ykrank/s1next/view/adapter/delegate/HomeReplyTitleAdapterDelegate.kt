package me.ykrank.s1next.view.adapter.delegate

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup

import me.ykrank.s1next.data.api.model.HomeThread
import me.ykrank.s1next.databinding.ItemHomeReplyTitleBinding
import me.ykrank.s1next.viewmodel.HomeReplyTitleViewModel

/**
 * Created by ykrank on 2017/2/4.
 */

class HomeReplyTitleAdapterDelegate(context: Context) : BaseAdapterDelegate<HomeThread, HomeReplyTitleAdapterDelegate.BindingViewHolder>(context, HomeThread::class.java) {

    override fun onBindViewHolderData(thread: HomeThread, position: Int, holder: BindingViewHolder, payloads: List<Any>) {
        val binding = holder.binding
        binding.model.thread.set(thread)
        binding.executePendingBindings()
    }

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val binding = ItemHomeReplyTitleBinding.inflate(mLayoutInflater, parent, false)
        binding.model = HomeReplyTitleViewModel()
        return HomeReplyTitleAdapterDelegate.BindingViewHolder(binding)
    }

    class BindingViewHolder(val binding: ItemHomeReplyTitleBinding) : RecyclerView.ViewHolder(binding.root)
}
