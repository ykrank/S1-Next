package me.ykrank.s1next.view.adapter

import android.app.Activity
import androidx.lifecycle.LifecycleOwner
import me.ykrank.s1next.view.adapter.delegate.FavouriteAdapterDelegate

class FavouriteRecyclerViewAdapter(
    activity: Activity,
    private val lifecycleOwner: LifecycleOwner,
) : BaseRecyclerViewAdapter(
    activity
) {
    init {
        addAdapterDelegate(FavouriteAdapterDelegate(activity, lifecycleOwner))
    }
}
