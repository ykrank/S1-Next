package me.ykrank.s1next.view.adapter

import android.app.Activity
import androidx.lifecycle.LifecycleOwner
import me.ykrank.s1next.view.adapter.delegate.ThreadAdapterDelegate

class ThreadRecyclerViewAdapter(
    activity: Activity,
    lifecycleOwner: LifecycleOwner,
    forumId: String?
) : BaseRecyclerViewAdapter(
    activity
) {
    init {
        addAdapterDelegate(ThreadAdapterDelegate(activity, lifecycleOwner, forumId))
    }
}
