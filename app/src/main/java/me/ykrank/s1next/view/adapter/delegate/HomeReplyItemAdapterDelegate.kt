package me.ykrank.s1next.view.adapter.delegate

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.ViewGroup
import com.github.ykrank.androidtools.ui.adapter.simple.SimpleRecycleViewHolder

import me.ykrank.s1next.data.api.model.HomeReply
import me.ykrank.s1next.databinding.ItemHomeReplyItemBinding
import me.ykrank.s1next.viewmodel.HomeReplyItemViewModel

/**
 * Created by ykrank on 2017/2/4.
 */

class HomeReplyItemAdapterDelegate(context: Context) : BaseAdapterDelegate<HomeReply, SimpleRecycleViewHolder<ItemHomeReplyItemBinding>>(context, HomeReply::class.java) {

    override fun onBindViewHolderData(t: HomeReply, position: Int, holder: SimpleRecycleViewHolder<ItemHomeReplyItemBinding>, payloads: List<Any>) {
        val binding = holder.binding
        binding.model?.reply?.set(t)
    }

    override fun onCreateViewHolder(parent: ViewGroup): androidx.recyclerview.widget.RecyclerView.ViewHolder {
        val binding = ItemHomeReplyItemBinding.inflate(mLayoutInflater, parent, false)
        binding.model = HomeReplyItemViewModel()
        return SimpleRecycleViewHolder<ItemHomeReplyItemBinding>(binding)
    }

}
