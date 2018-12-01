package me.ykrank.s1next.view.adapter.delegate

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.github.ykrank.androidtools.ui.adapter.simple.SimpleRecycleViewHolder

import me.ykrank.s1next.data.api.model.Friend
import me.ykrank.s1next.databinding.ItemFriendBinding
import me.ykrank.s1next.viewmodel.FriendViewModel

/**
 * Created by ykrank on 2017/1/16.
 */

class FriendAdapterDelegate(context: Context) : BaseAdapterDelegate<Friend, SimpleRecycleViewHolder<ItemFriendBinding>>(context, Friend::class.java) {

    override fun onBindViewHolderData(t: Friend, position: Int, holder: SimpleRecycleViewHolder<ItemFriendBinding>, payloads: List<Any>) {
        val binding = holder.binding
        binding.data?.friend?.set(t)
    }

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val binding = ItemFriendBinding.inflate(mLayoutInflater, parent, false)
        binding.data = FriendViewModel()
        return SimpleRecycleViewHolder<ItemFriendBinding>(binding)
    }
}
