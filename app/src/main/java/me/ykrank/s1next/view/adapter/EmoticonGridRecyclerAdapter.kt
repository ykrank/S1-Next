package me.ykrank.s1next.view.adapter

import android.app.Activity
import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup

import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.github.ykrank.androidtools.widget.RxBus

import me.ykrank.s1next.App
import me.ykrank.s1next.R
import me.ykrank.s1next.data.api.model.Emoticon
import me.ykrank.s1next.databinding.ItemEmoticonBinding
import me.ykrank.s1next.viewmodel.EmoticonViewModel

class EmoticonGridRecyclerAdapter(activity: Activity, private val mEmoticons: List<Emoticon>) : RecyclerView.Adapter<EmoticonGridRecyclerAdapter.BindingViewHolder>() {

    private val mLayoutInflater: LayoutInflater = activity.layoutInflater
    private val mEmoticonRequestBuilder: RequestManager = Glide.with(activity)

    private val mRxBus: RxBus = App.preAppComponent.rxBus

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BindingViewHolder {
        val binding = DataBindingUtil.inflate<ItemEmoticonBinding>(mLayoutInflater,
                R.layout.item_emoticon, parent, false)
        binding.rxBus = mRxBus
        binding.requestManager = mEmoticonRequestBuilder
        binding.emoticonViewModel = EmoticonViewModel()

        return BindingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BindingViewHolder, position: Int) {
        holder.itemEmoticonBinding.emoticonViewModel?.emoticon?.set(mEmoticons[position])
        holder.itemEmoticonBinding.executePendingBindings()
    }

    override fun onViewRecycled(holder: BindingViewHolder) {
        super.onViewRecycled(holder)
    }

    override fun getItemCount(): Int {
        return mEmoticons.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    class BindingViewHolder(val itemEmoticonBinding: ItemEmoticonBinding) : RecyclerView.ViewHolder(itemEmoticonBinding.root)
}
