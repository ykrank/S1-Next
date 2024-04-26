package me.ykrank.s1next.view.page.post.viewmodel

import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.PopupMenu
import androidx.databinding.Observable
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import com.github.ykrank.androidtools.util.ContextUtils
import com.github.ykrank.androidtools.util.L
import com.github.ykrank.androidtools.widget.RxBus
import me.ykrank.s1next.R
import me.ykrank.s1next.data.User
import me.ykrank.s1next.data.api.Api
import me.ykrank.s1next.data.api.model.Post
import me.ykrank.s1next.data.api.model.Thread
import me.ykrank.s1next.data.api.model.Vote
import me.ykrank.s1next.view.activity.UserHomeActivity
import me.ykrank.s1next.view.activity.WebViewActivity
import me.ykrank.s1next.view.event.*
import me.ykrank.s1next.view.internal.BlacklistMenuAction
import me.ykrank.s1next.view.page.app.AppPostListActivity
import me.ykrank.s1next.view.page.post.PostListActivity
import me.ykrank.s1next.widget.glide.AvatarUrlsCache

class PostViewModel(private val rxBus: RxBus, private val user: User) {

    val post = ObservableField<Post>()
    val thread = ObservableField<Thread>()
    val vote = ObservableField<Vote>()
    val floor = ObservableField<CharSequence>()
    val pageNum = ObservableInt()

    private val postFloor: CharSequence?
        get() {
            val p = post.get() ?: return null
            return "#${p.number}"
        }

    init {
        post.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(observable: Observable, i: Int) {
                floor.set(postFloor)
            }
        })
    }

    fun onAvatarClick(v: View) {
        post.get()?.let {
            val authorId = it.authorId
            val authorName = it.authorName
            if (authorId != null && authorName != null) {
                //Clear avatar false cache
                AvatarUrlsCache.clearUserAvatarCache(authorId)
                //个人主页
                UserHomeActivity.start(v.context as androidx.fragment.app.FragmentActivity, authorId, authorName, v)
            }
        }
    }

    fun onAvatarLongClick(v: View): Boolean {
        //长按显示抹布菜单
        val popup = PopupMenu(v.context, v)
        val postData = post.get()
        popup.setOnMenuItemClickListener { menuitem: MenuItem ->
            when (menuitem.itemId) {
                R.id.menu_popup_blacklist -> {
                    val authorId = postData?.authorId
                    if (!authorId.isNullOrBlank()) {
                        val authorIdInt = authorId!!.toInt()
                        val authorName = postData.authorName
                        if (authorName != null) {
                            if (menuitem.title == v.context.getString(R.string.menu_blacklist_remove)) {
                                BlacklistMenuAction.removeBlacklist(rxBus, authorIdInt, authorName)
                            } else {
                                val context = ContextUtils.getBaseContext(v.context)
                                if (context is androidx.fragment.app.FragmentActivity) {
                                    BlacklistMenuAction.addBlacklist(context, authorIdInt, authorName)
                                } else {
                                    L.report(IllegalStateException("抹布时头像Context不为FragmentActivity$context"))
                                }
                            }
                        }
                    }

                    return@setOnMenuItemClickListener true
                }
                else -> return@setOnMenuItemClickListener false
            }
        }
        popup.inflate(R.menu.popup_blacklist)
        if (postData?.hide == Post.HIDE_USER) {
            popup.menu.findItem(R.id.menu_popup_blacklist).setTitle(R.string.menu_blacklist_remove)
        }
        popup.show()
        return true
    }

    //click floor textView, show popup menu
    fun showFloorActionMenu(v: View) {
        val popup = PopupMenu(v.context, v)
        popup.setOnMenuItemClickListener { menuitem: MenuItem ->
            when (menuitem.itemId) {
                R.id.menu_popup_reply -> {
                    onReplyClick(v)
                    return@setOnMenuItemClickListener true
                }
                R.id.menu_popup_rate -> {
                    onRateClick(v)
                    return@setOnMenuItemClickListener true
                }
                R.id.menu_popup_edit -> {
                    onEditClick(v)
                    return@setOnMenuItemClickListener true
                }
                R.id.menu_popup_report -> {
                    onReportClick(v)
                    return@setOnMenuItemClickListener true
                }
                else -> return@setOnMenuItemClickListener false
            }
        }
        popup.inflate(R.menu.popup_post_floor)

        val editPostMenuItem = popup.menu.findItem(R.id.menu_popup_edit)
        editPostMenuItem.isVisible = user.isLogged && user.uid == post.get()?.authorId
        popup.show()
    }

    fun onReplyClick(v: View) {
        val postId = post.get()?.id?.toString()
        val count = post.get()?.number
        if (postId != null && count != null) {
            rxBus.post(QuoteEvent(postId, count))
        }
    }

    fun onRateClick(v: View) {
        val tid = thread.get()?.id
        val pid = post.get()?.id?.toString()
        if (tid != null && pid != null) {
            rxBus.post(RateEvent(tid, pid))
        }
    }

    fun onReportClick(v: View) {
        val tid = thread.get()?.id
        val pid = post.get()?.id?.toString()
        if (tid != null && pid != null) {
            rxBus.post(ReportEvent(tid, pid, pageNum.get()))
        }
    }

    fun onEditClick(v: View) {
        val p = post.get()
        val t = thread.get()
        if (p != null && t != null) {
            rxBus.post(EditPostEvent(p, t))
        }
    }

    fun onExtraHtmlClick(v: View) {
        val p = post.get()
        val t = thread.get()
        if (p != null && t != null) {
            val url = "${Api.BASE_URL}forum.php?mod=viewthread&do=tradeinfo&tid=${t.id}&pid=${p.id + 1}"
            WebViewActivity.start(v.context, url, true, true)
        }
    }

    fun onVoteClick(v: View) {
        val tid = thread.get()?.id
        val vo = vote.get()
        if (tid != null && vo != null) {
            rxBus.post(VotePostEvent(tid, vo))
        }
    }

    fun onAppPostClick(v: View) {
        val p = post.get()
        val t = thread.get()
        if (p != null && t?.id != null) {
            AppPostListActivity.start(v.context, t.id!!, p.getPage(), p.id.toString())
        }
    }

    fun onOnlySeeHimClick(v: View) {
        val aId = post.get()?.authorId
        val t = thread.get()
        if (aId != null && t != null) {
            PostListActivity.start(v.context, t, aId)
        }
    }

}
