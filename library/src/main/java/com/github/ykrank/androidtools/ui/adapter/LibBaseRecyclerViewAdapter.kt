package com.github.ykrank.androidtools.ui.adapter

import android.content.Context
import androidx.recyclerview.widget.DiffUtil
import com.github.ykrank.androidtools.BuildConfig
import com.google.common.base.Objects
import com.google.common.base.Preconditions
import com.github.ykrank.androidtools.ui.adapter.delegate.FooterAdapterDelegate
import com.github.ykrank.androidtools.ui.adapter.delegate.FooterProgressAdapterDelegate
import com.github.ykrank.androidtools.ui.adapter.delegate.ProgressAdapterDelegate
import com.github.ykrank.androidtools.ui.adapter.delegate.item.FooterProgressItem
import com.github.ykrank.androidtools.ui.adapter.delegate.item.ProgressItem
import com.github.ykrank.androidtools.ui.adapter.model.DiffSameItem
import com.github.ykrank.androidtools.util.L
import com.github.ykrank.androidtools.util.RxJavaUtil
import com.hannesdorfmann.adapterdelegates3.AdapterDelegate
import com.hannesdorfmann.adapterdelegates3.ListDelegationAdapter
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import java.util.concurrent.atomic.AtomicBoolean

abstract class LibBaseRecyclerViewAdapter : ListDelegationAdapter<MutableList<Any>> {

    var updateDispose: Disposable? = null

    /**
     * Whether diffutil calculateDiffing
     */
    private var differing: AtomicBoolean = AtomicBoolean(false)

    constructor(context: Context) : this(context, true)

    /**
     * Only use when you sure inner model implements [StableIdModel]
     */
    constructor(context: Context, stableId: Boolean) {
        setHasStableIds(stableId)
        items = arrayListOf()
        delegatesManager.addDelegate(VIEW_TYPE_PROGRESS, ProgressAdapterDelegate(context))
        addAdapterDelegate(FooterProgressAdapterDelegate(context))
        addAdapterDelegate(FooterAdapterDelegate(context))
    }

    protected fun addAdapterDelegate(adapterDelegate: AdapterDelegate<MutableList<Any>>) {
        Preconditions.checkArgument(delegatesManager.getViewType(adapterDelegate) != VIEW_TYPE_PROGRESS)
        delegatesManager.addDelegate(adapterDelegate)
    }

    fun setHasProgress(hasProgress: Boolean) {
        if (hasProgress) {
            //If diffing, post a task to it
            if (differing.get()) {
                diffNewDataSet(listOf(ProgressItem()), false)
            } else {
                clear()
                addItem(ProgressItem())
                notifyDataSetChanged()
            }
        } else {
            // we do not need to clear list if we have already changed
            // data set or we have no ProgressItem to been cleared or differing
            if (!differing.get() && items.size == 1 && items[0] is ProgressItem) {
                clear()
                notifyDataSetChanged()
            }
        }
    }

    fun showFooterProgress() {
        if (differing.get()) {
            return
        }
        addItem(FooterProgressItem())
        notifyItemInserted(items.size)
    }

    fun hideFooterProgress() {
        if (differing.get()) {
            return
        }
        val position = itemCount - 1
        val lastItem = getItem(position)
        if (lastItem is FooterProgressItem) {
            removeItem(position)
            notifyItemRemoved(position)
        }
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
    fun diffNewDataSet(newData: List<Any>, detectMoves: Boolean, callback: (() -> Unit)? = null) {
        if (items === newData) {
            refreshDataSet(newData, detectMoves)
            L.throwNewErrorIfDebug(IllegalArgumentException("must set new data set"))
            return
        }
        RxJavaUtil.disposeIfNotNull(updateDispose)

        differing.set(true)
        updateDispose = Single.just(BaseDiffCallback(items, newData))
                .map { DiffUtil.calculateDiff(it, detectMoves) }
                .compose(RxJavaUtil.iOSingleTransformer())
                .doFinally { differing.set(false) }
                .subscribe({
                    items = newData.toMutableList()
                    it.dispatchUpdatesTo(this)
                    callback?.invoke()
                }, L::report)
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

    /**
     * Any change on items should run on main thread and not differing
     */
    fun addItem(`object`: Any) {
        checkNotDiffering()
        items.add(`object`)
    }

    /**
     * Any change on items should run on main thread and not differing
     */
    fun clear() {
        checkNotDiffering()
        items.clear()
    }

    /**
     * Any change on items should run on main thread and not differing
     */
    fun removeItem(position: Int) {
        checkNotDiffering()
        items.removeAt(position)
    }

    /**
     * Any change on items should run on main thread and not differing
     */
    private fun checkNotDiffering() {
        if (L.showLog()) {
            check(!differing.get())
        }
    }

    override fun getItemId(position: Int): Long {
        if (!hasStableIds()) {
            return super.getItemId(position)
        }

        val d = items[position]
        if (d is StableIdModel) {
            return d.stableId
        }
        if (BuildConfig.DEBUG) {
            throw IllegalStateException("Item must implements StableIdModel if stable id")
        }
        return Objects.hashCode(d).toLong()
    }

    class BaseDiffCallback(var oldData: List<*>, var newData: List<*>) : DiffUtil.Callback() {

        override fun getOldListSize(): Int {
            return oldData.size
        }

        override fun getNewListSize(): Int {
            return newData.size
        }

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldD = oldData.getOrNull(oldItemPosition)
            val newD = newData.getOrNull(newItemPosition)
            if (oldD != null && oldD is DiffSameItem) {
                return oldD.isSameItem(newD)
            }
            return oldD == newD
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldD = oldData.getOrNull(oldItemPosition)
            val newD = newData.getOrNull(newItemPosition)
            if (oldD != null && oldD is DiffSameItem) {
                return oldD.isSameContent(newD)
            }
            return oldD == newD
        }

        override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
            val oldD = oldData.getOrNull(oldItemPosition)
            val newD = newData.getOrNull(newItemPosition)
            if (oldD != null && oldD is DiffSameItem) {
                return oldD.getChangePayload(newD)
            }
            return null
        }
    }

    companion object {

        private val VIEW_TYPE_PROGRESS = 0
    }
}
