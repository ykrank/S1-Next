package me.ykrank.s1next.view.setting.blacklist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import me.ykrank.s1next.R
import me.ykrank.s1next.data.db.dbmodel.BlackList
import me.ykrank.s1next.databinding.ItemBlacklistBinding
import me.ykrank.s1next.viewmodel.BlackListViewModel

class BlackListPagingAdapter :
    PagingDataAdapter<BlackList, BlackListPagingAdapter.ViewHolder>(DIFF_CALLBACK) {
    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<BlackList>() {
            override fun areItemsTheSame(oldItem: BlackList, newItem: BlackList): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: BlackList, newItem: BlackList): Boolean =
                oldItem == newItem
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.binding.blackListViewModel?.blacklist?.set(item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemBlacklistBinding = DataBindingUtil.inflate<ItemBlacklistBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_blacklist, parent, false
        )
        itemBlacklistBinding.blackListViewModel = BlackListViewModel()
        return ViewHolder(itemBlacklistBinding)
    }

    inner class ViewHolder(val binding: ItemBlacklistBinding) :
        RecyclerView.ViewHolder(binding.root)
}