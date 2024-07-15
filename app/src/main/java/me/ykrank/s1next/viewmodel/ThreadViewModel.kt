package me.ykrank.s1next.viewmodel

import android.view.View
import android.view.View.OnLongClickListener
import androidx.databinding.ObservableField
import androidx.lifecycle.LifecycleOwner
import me.ykrank.s1next.data.api.model.Thread
import me.ykrank.s1next.view.page.post.postlist.PostListActivity.Companion.bindClickStartForView
import me.ykrank.s1next.view.page.post.postlist.PostListActivity.Companion.start

class ThreadViewModel(private val lifecycleOwner: LifecycleOwner) {
    val thread = ObservableField<Thread>()
    fun onBind(): Function1<View, Any> {
        return { v: View ->
            bindClickStartForView(
                v, lifecycleOwner
            ) {
                thread.get()
            }
        }
    }

    fun goToThisThreadLastPage(): OnLongClickListener {
        return OnLongClickListener { v: View ->
            thread.get()?.apply {
                start(v.context, this, true)
            }
            true
        }
    }
}
