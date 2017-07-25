package me.ykrank.s1next.view.adapter

import android.content.Context
import android.support.v7.util.DiffUtil
import com.google.common.base.Preconditions
import com.hannesdorfmann.adapterdelegates3.AdapterDelegate
import com.hannesdorfmann.adapterdelegates3.ListDelegationAdapter
import me.ykrank.s1next.data.SameItem
import me.ykrank.s1next.util.ErrorUtil
import me.ykrank.s1next.view.adapter.delegate.ProgressAdapterDelegate
import me.ykrank.s1next.view.adapter.item.FooterProgressItem
import me.ykrank.s1next.view.adapter.item.ProgressItem
import java.util.*

abstract class BaseRecyclerViewAdapter(context: Context) : ListDelegationAdapter<MutableList<Any>>() {

    init {
        setHasStableIds(false)
        setItems(ArrayList<Any>())
        delegatesManager.addDelegate(VIEW_TYPE_PROGRESS, ProgressAdapterDelegate(context))
    }

    protected fun addAdapterDelegate(adapterDelegate: AdapterDelegate<MutableList<Any>>) {
        Preconditions.checkArgument(delegatesManager.getViewType(adapterDelegate) != VIEW_TYPE_PROGRESS)
        delegatesManager.addDelegate(adapterDelegate)
    }

    fun setHasProgress(hasProgress: Boolean) {
        if (hasProgress) {
            items.clear()
            items.add(ProgressItem())
            notifyDataSetChanged()
        } else {
            // we do not need to clear list if we have already changed
            // data set or we have no ProgressItem to been cleared
            if (items.size == 1 && items[0] is ProgressItem) {
                items.clear()
                notifyDataSetChanged()
            }
        }
    }

    fun showFooterProgress() {
        val position = itemCount - 1
        Preconditions.checkState(getItem(position) != null)
        addItem(FooterProgressItem())
        notifyItemInserted(position + 1)
    }

    fun hideFooterProgress() {
        val position = itemCount - 1
        Preconditions.checkState(getItem(position) is FooterProgressItem)
        removeItem(position)
        notifyItemRemoved(position)
    }

    /**
     * diff new dataSet with old, and dispatch update.\n
     * must another object with old.

     * @param newData     new data set
     * *
     * @param detectMoves [DiffUtil.calculateDiff]
     * *
     * @see DiffUtil
     */
    fun diffNewDataSet(newData: List<Any>, detectMoves: Boolean) {
        if (items === newData) {
            refreshDataSet(newData, detectMoves)
            ErrorUtil.throwNewErrorIfDebug(IllegalArgumentException("must set new data set"))
        }
        val diffResult = DiffUtil.calculateDiff(
                BaseDiffCallback(items, newData), detectMoves)
        items = newData.toMutableList()
        diffResult.dispatchUpdatesTo(this)
    }

    /**
     * refresh new dataSet.if same object, just notifyDataSetChanged, else [.diffNewDataSet]

     * @param newData     new data set
     * *
     * @param detectMoves [.diffNewDataSet]
     */
    fun refreshDataSet(newData: List<Any>, detectMoves: Boolean) {
        if (items !== newData) {
            diffNewDataSet(newData, detectMoves)
        } else {
            notifyDataSetChanged()
        }
    }

    /**
     * swap to new dataSet.only notifyDataSetChanged

     * @param newData new data set
     */
    fun swapDataSet(newData: List<Any>) {
        items = newData.toMutableList()
        notifyDataSetChanged()
    }

    val dataSet: List<Any>
        get() = getItems()

    fun getItem(position: Int): Any? {
        return items[position]
    }

    fun addItem(`object`: Any) {
        items.add(`object`)
    }

    fun removeItem(position: Int) {
        items.removeAt(position)
    }

    class BaseDiffCallback(var oldData: List<*>, var newData: List<*>) : DiffUtil.Callback() {

        override fun getOldListSize(): Int {
            return oldData.size
        }

        override fun getNewListSize(): Int {
            return newData.size
        }

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldD = oldData[oldItemPosition]
            val newD = newData[newItemPosition]
            if (oldD != null && oldD is SameItem) {
                return oldD.isSameItem(newD)
            }
            return oldD == newD
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldData[oldItemPosition] == newData[newItemPosition]
        }
    }

    companion object {

        private val VIEW_TYPE_PROGRESS = 0
    }
}
