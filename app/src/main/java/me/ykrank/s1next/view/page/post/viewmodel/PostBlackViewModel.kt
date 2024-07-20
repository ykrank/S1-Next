package me.ykrank.s1next.view.page.post.viewmodel

import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.PopupMenu
import androidx.databinding.Observable
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import com.github.ykrank.androidtools.util.ContextUtils
import com.github.ykrank.androidtools.util.L
import com.github.ykrank.androidtools.widget.EventBus
import me.ykrank.s1next.R
import me.ykrank.s1next.data.api.model.Post
import me.ykrank.s1next.data.api.model.Thread
import me.ykrank.s1next.data.api.model.Vote
import me.ykrank.s1next.view.activity.UserHomeActivity
import me.ykrank.s1next.view.internal.BlacklistMenuAction
import me.ykrank.s1next.widget.glide.AvatarUrlsCache

class PostBlackViewModel(val lifecycleOwner: LifecycleOwner, private val eventBus: EventBus) {

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
                UserHomeActivity.start(
                    v.context as FragmentActivity,
                    authorId,
                    authorName,
                    v
                )
            }
        }
    }

    fun onAvatarLongClick(v: View): Boolean {
        return showBlackListMenu(v)
    }

    fun onFloorClick(v: View) {
        showBlackListMenu(v)
    }

    fun showBlackListMenu(v: View): Boolean {
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
                                BlacklistMenuAction.removeBlacklist(lifecycleOwner, eventBus, authorIdInt, authorName)
                            } else {
                                val context = ContextUtils.getBaseContext(v.context)
                                if (context is androidx.fragment.app.FragmentActivity) {
                                    BlacklistMenuAction.addBlacklist(
                                        context,
                                        authorIdInt,
                                        authorName
                                    )
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


}
