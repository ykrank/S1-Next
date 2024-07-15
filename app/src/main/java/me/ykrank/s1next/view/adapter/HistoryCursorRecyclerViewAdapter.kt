package me.ykrank.s1next.view.adapter

import android.app.Activity
import android.database.Cursor
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import com.github.ykrank.androidtools.ui.adapter.simple.SimpleRecycleViewHolder
import me.ykrank.s1next.data.db.biz.HistoryBiz.Companion.instance
import me.ykrank.s1next.databinding.ItemHistoryBinding
import me.ykrank.s1next.viewmodel.HistoryViewModel

class HistoryCursorRecyclerViewAdapter(
    activity: Activity,
    private val lifecycleOwner: LifecycleOwner,
) :
    CursorRecyclerViewAdapter<SimpleRecycleViewHolder<ItemHistoryBinding>>(activity, null) {
    private val mLayoutInflater: LayoutInflater

    init {
        mLayoutInflater = activity.layoutInflater
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SimpleRecycleViewHolder<ItemHistoryBinding> {
        val binding = ItemHistoryBinding.inflate(mLayoutInflater, parent, false)
        binding.setModel(HistoryViewModel(lifecycleOwner))
        return SimpleRecycleViewHolder(binding)
    }

    override fun onBindViewHolder(
        viewHolder: SimpleRecycleViewHolder<ItemHistoryBinding>,
        cursor: Cursor
    ) {
        val binding = viewHolder.binding
        binding.getModel()?.history?.set(instance.fromCursor(cursor))
    }
}
