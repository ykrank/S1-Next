package me.ykrank.s1next.viewmodel

import androidx.databinding.Observable
import androidx.databinding.ObservableField
import androidx.fragment.app.FragmentActivity
import androidx.appcompat.widget.PopupMenu
import android.view.MenuItem
import android.view.View
import com.github.ykrank.androidtools.util.ContextUtils
import com.github.ykrank.androidtools.util.L
import com.github.ykrank.androidtools.widget.RxBus
import me.ykrank.s1next.R
import me.ykrank.s1next.data.User
import me.ykrank.s1next.data.api.Api
import me.ykrank.s1next.data.api.app.model.AppPost
import me.ykrank.s1next.data.api.app.model.AppThread
import me.ykrank.s1next.view.activity.UserHomeActivity
import me.ykrank.s1next.view.activity.WebViewActivity
import me.ykrank.s1next.view.event.EditAppPostEvent
import me.ykrank.s1next.view.event.QuoteEvent
import me.ykrank.s1next.view.event.RateEvent
import me.ykrank.s1next.view.internal.BlacklistMenuAction
import me.ykrank.s1next.widget.glide.AvatarUrlsCache

class AppPostViewModel(private val rxBus: RxBus, private val user: User) {

    val post = ObservableField<AppPost>()
    val thread = ObservableField<AppThread>()
    val floor = ObservableField<CharSequence>()

    private val postFloor: CharSequence?
        get() {
            val p = post.get() ?: return null
            return "#${p.position}"
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
            //Clear avatar false cache
            AvatarUrlsCache.clearUserAvatarCache("" + it.authorId)
            //个人主页
            UserHomeActivity.start(v.context as androidx.fragment.app.FragmentActivity, "" + it.authorId, it.author, v)
        }
    }

    fun onLongClick(v: View): Boolean {
        //长按显示抹布菜单
        val popup = PopupMenu(v.context, v)
        val postData = post.get()
        popup.setOnMenuItemClickListener { menuitem: MenuItem ->
            when (menuitem.itemId) {
                R.id.menu_popup_blacklist -> {
                    if (menuitem.title == v.context.getString(R.string.menu_blacklist_remove)) {
                        BlacklistMenuAction.removeBlacklist(rxBus, postData?.authorId
                                ?: 0, postData?.author)
                    } else {
                        val context = ContextUtils.getBaseContext(v.context)
                        if (context is androidx.fragment.app.FragmentActivity) {
                            BlacklistMenuAction.addBlacklist(context,
                                    postData?.authorId ?: 0, postData?.author)
                        } else {
                            L.report(IllegalStateException("抹布时头像Context不为FragmentActivity$context"))
                        }
                    }
                    return@setOnMenuItemClickListener true
                }
                else -> return@setOnMenuItemClickListener false
            }
        }
        popup.inflate(R.menu.popup_blacklist)
        if (postData?.hide == true) {
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
                else -> return@setOnMenuItemClickListener false
            }
        }
        popup.inflate(R.menu.popup_post_floor)

        val editPostMenuItem = popup.menu.findItem(R.id.menu_popup_edit)
        editPostMenuItem.isVisible = user.isLogged && user.uid == post.get()?.authorId?.toString()
        popup.show()
    }

    fun onReplyClick(v: View) {
        post.get()?.let {
            rxBus.post(QuoteEvent(it.pid.toString(), it.position.toString()))
        }
    }

    fun onRateClick(v: View) {
        post.get()?.let {
            rxBus.post(RateEvent(it.tid.toString(), it.pid.toString()))
        }
    }

    fun onEditClick(v: View) {
        val p = post.get()
        val t = thread.get()
        if (p != null && t != null) {
            rxBus.post(EditAppPostEvent(p, t))
        }
    }

    fun onTradeHtmlClick(v: View) {
        post.get()?.let {
            val url = String.format("%sforum.php?mod=viewthread&do=tradeinfo&tid=%s&pid=%s", Api.BASE_URL, it.tid, it.pid + 1)
            WebViewActivity.start(v.context, url, true, true)
        }
    }
}
