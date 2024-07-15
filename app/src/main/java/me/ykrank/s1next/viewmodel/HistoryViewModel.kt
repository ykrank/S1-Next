package me.ykrank.s1next.viewmodel

import android.view.View
import androidx.databinding.ObservableField
import androidx.lifecycle.LifecycleOwner
import me.ykrank.s1next.data.api.model.Thread
import me.ykrank.s1next.data.db.dbmodel.History
import me.ykrank.s1next.view.page.post.postlist.PostListActivity.Companion.bindClickStartForView

class HistoryViewModel(private val lifecycleOwner: LifecycleOwner) {
    val history = ObservableField<History>()

    fun onBind(): Function1<View, Any> {
        return { v: View ->
            bindClickStartForView(
                v, lifecycleOwner
            ) {
                history.get()?.let {
                    Thread(it)
                }
            }
        }
    }
}
