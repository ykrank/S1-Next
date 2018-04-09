package me.ykrank.s1next.view.adapter.delegate

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup

import me.ykrank.s1next.data.api.model.HomeReply
import me.ykrank.s1next.databinding.ItemHomeReplyItemBinding
import me.ykrank.s1next.viewmodel.HomeReplyItemViewModel

/**
 * Created by ykrank on 2017/2/4.
 */

class HomeReplyItemAdapterDelegate(context: Context) : BaseAdapterDelegate<HomeReply, HomeReplyItemAdapterDelegate.BindingViewHolder>(context, HomeReply::class.java) {

    override fun onBindViewHolderData(t: HomeReply, position: Int, holder: BindingViewHolder, payloads: List<Any>) {
        val binding = holder.binding
        binding.model?.reply?.set(t)
        binding.executePendingBindings()
    }

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val binding = ItemHomeReplyItemBinding.inflate(mLayoutInflater, parent, false)
        binding.model = HomeReplyItemViewModel()
        return HomeReplyItemAdapterDelegate.BindingViewHolder(binding)
    }

    class BindingViewHolder(val binding: ItemHomeReplyItemBinding) : RecyclerView.ViewHolder(binding.root)
}
