package me.ykrank.s1next.view.adapter

import android.app.Activity
import androidx.lifecycle.LifecycleOwner
import me.ykrank.s1next.view.adapter.delegate.PmLeftAdapterDelegate
import me.ykrank.s1next.view.adapter.delegate.PmRightAdapterDelegate

class PmRecyclerViewAdapter(activity: Activity, lifecycleOwner: LifecycleOwner) :
    BaseRecyclerViewAdapter(activity) {
    init {
        addAdapterDelegate(PmLeftAdapterDelegate(activity, lifecycleOwner))
        addAdapterDelegate(PmRightAdapterDelegate(activity, lifecycleOwner))
    }
}
