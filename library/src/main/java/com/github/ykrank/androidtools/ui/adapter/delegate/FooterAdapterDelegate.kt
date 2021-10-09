package com.github.ykrank.androidtools.ui.adapter.delegate

import android.content.Context
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import android.view.ViewGroup
import com.github.ykrank.androidtools.R
import com.github.ykrank.androidtools.databinding.ItemFooterBinding
import com.github.ykrank.androidtools.ui.adapter.delegate.item.FooterItem


class FooterAdapterDelegate(context: Context) : LibBaseAdapterDelegate<FooterItem,
        FooterAdapterDelegate.FooterViewHolder>(context, FooterItem::class.java) {

    public override fun onCreateViewHolder(parent: ViewGroup): androidx.recyclerview.widget.RecyclerView.ViewHolder {
        return FooterViewHolder(DataBindingUtil.inflate(mLayoutInflater, R.layout.item_footer, parent, false))
    }

    override fun onBindViewHolderData(t: FooterItem, position: Int, holder: FooterViewHolder, payloads: List<Any>) {

    }

    class FooterViewHolder(private val binding: ItemFooterBinding) : androidx.recyclerview.widget.RecyclerView.ViewHolder(binding.root)
}
