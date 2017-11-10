package me.ykrank.s1next.view.adapter.delegate

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup

import me.ykrank.s1next.data.api.model.Friend
import me.ykrank.s1next.databinding.ItemFriendBinding
import me.ykrank.s1next.viewmodel.FriendViewModel

/**
 * Created by ykrank on 2017/1/16.
 */

class FriendAdapterDelegate(context: Context) : BaseAdapterDelegate<Friend, FriendAdapterDelegate.BindingViewHolder>(context, Friend::class.java) {

    override fun onBindViewHolderData(friend: Friend, position: Int, holder: BindingViewHolder, payloads: List<Any>) {
        val binding = holder.binding
        binding.data.friend.set(friend)
        binding.executePendingBindings()
    }

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val binding = ItemFriendBinding.inflate(mLayoutInflater, parent, false)
        binding.data = FriendViewModel()
        return BindingViewHolder(binding)
    }

    class BindingViewHolder(val binding: ItemFriendBinding) : RecyclerView.ViewHolder(binding.root)
}
