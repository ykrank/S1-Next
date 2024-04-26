package me.ykrank.s1next.view.page.post.adapter

import android.content.Context
import androidx.fragment.app.Fragment
import me.ykrank.s1next.data.api.model.Thread
import me.ykrank.s1next.data.api.model.Vote
import me.ykrank.s1next.view.adapter.BaseRecyclerViewAdapter

/**
 * This [RecyclerView.Adapter]
 * has another item type [FooterProgressAdapterDelegate]
 * in order to implement pull up to refresh.
 */
class PostListRecyclerViewAdapter(fragment: Fragment, context: Context) :
    BaseRecyclerViewAdapter(context, true) {
    private val postAdapterDelegate = PostAdapterDelegate(fragment, context)
    private val postBlackAdapterDelegate = PostBlackAdapterDelegate(fragment, context)

    init {
        addAdapterDelegate(postAdapterDelegate)
        addAdapterDelegate(postBlackAdapterDelegate)
    }

    fun setThreadInfo(threadInfo: Thread, pageNum: Int) {
        postAdapterDelegate.setThreadInfo(threadInfo, pageNum)
    }

    fun setVoteInfo(voteInfo: Vote?) {
        postAdapterDelegate.setVoteInfo(voteInfo)
    }
}
