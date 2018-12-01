package me.ykrank.s1next.view.adapter.delegate

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.github.ykrank.androidtools.ui.adapter.simple.SimpleRecycleViewHolder

import me.ykrank.s1next.data.api.model.HomeThread
import me.ykrank.s1next.databinding.ItemHomeReplyTitleBinding
import me.ykrank.s1next.viewmodel.HomeReplyTitleViewModel

/**
 * Created by ykrank on 2017/2/4.
 */

class HomeReplyTitleAdapterDelegate(context: Context) : BaseAdapterDelegate<HomeThread, SimpleRecycleViewHolder<ItemHomeReplyTitleBinding>>(context, HomeThread::class.java) {

    override fun onBindViewHolderData(t: HomeThread, position: Int, holder: SimpleRecycleViewHolder<ItemHomeReplyTitleBinding>, payloads: List<Any>) {
        val binding = holder.binding
        binding.model?.thread?.set(t)
    }

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val binding = ItemHomeReplyTitleBinding.inflate(mLayoutInflater, parent, false)
        binding.model = HomeReplyTitleViewModel()
        return SimpleRecycleViewHolder<ItemHomeReplyTitleBinding>(binding)
    }

}
