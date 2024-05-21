package me.ykrank.s1next.viewmodel

import android.view.View
import androidx.databinding.ObservableField
import me.ykrank.s1next.data.api.Api
import me.ykrank.s1next.data.api.app.model.AppVote
import me.ykrank.s1next.data.api.model.Vote
import me.ykrank.s1next.view.activity.WebViewActivity.Companion.start

class VoteViewModel(private val vote: Vote, private val action: VoteVmAction?) {
    @JvmField
    val appVote = ObservableField<AppVote>()
    fun getVoteSummary(appVote: AppVote?): String {
        if (appVote == null) {
            return "加载数据中..."
        }
        val builder = StringBuilder()
        if (appVote.isMultiple) {
            builder.append("多选投票: ( 最多可选 ").append(appVote.maxChoices).append(" 项 ) , ")
        } else {
            builder.append("单选投票, ")
        }
        if (!appVote.isVisible) {
            builder.append("投票后结果可见, ")
        }
        if (appVote.isOvert) {
            builder.append("公开投票, ")
        }
        builder.append(" 共有 ").append(appVote.voters).append(" 人参与投票。")
        if (appVote.isVoted) {
            builder.append(" 你已投票。")
        }
        return builder.toString()
    }

    fun isVoteable(appVote: AppVote?): Boolean {
        return appVote != null && vote.isAllow && !appVote.isVoted
    }

    val isVoteable: Boolean
        get() = isVoteable(appVote.get())
    val isMultiple: Boolean
        get() = vote.isMultiple

    fun clickViewAllVoter(appVote: AppVote?): View.OnClickListener {
        return View.OnClickListener { v: View ->
            if (appVote == null) {
                return@OnClickListener
            }
            start(
                v.context,
                Api.URL_VIEW_VOTE + "&tid=" + appVote.tid,
                true,
                true
            )
        }
    }

    fun clickVote(): View.OnClickListener {
        return View.OnClickListener { v: View? -> action?.onClickVote(v) }
    }

    interface VoteVmAction {
        fun onClickVote(view: View?)
    }
}
