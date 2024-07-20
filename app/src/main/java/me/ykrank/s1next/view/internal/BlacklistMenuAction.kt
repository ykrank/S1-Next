package me.ykrank.s1next.view.internal

import androidx.annotation.MainThread
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.github.ykrank.androidtools.widget.EventBus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.ykrank.s1next.App
import me.ykrank.s1next.data.db.biz.BlackListBiz
import me.ykrank.s1next.view.page.setting.blacklist.BlackListRemarkDialogFragment
import me.ykrank.s1next.view.event.BlackListChangeEvent
import me.ykrank.s1next.widget.track.event.BlackListTrackEvent

/**
 * Action when click black add/remove in menu
 * Created by ykrank on 2017/3/19.
 */
object BlacklistMenuAction {
    @MainThread
    fun addBlacklist(activity: FragmentActivity, uid: Int, name: String?) {
        BlackListRemarkDialogFragment.newInstance(uid, name)
            .show(activity.supportFragmentManager, BlackListRemarkDialogFragment.TAG)
    }

    @MainThread
    fun removeBlacklist(lifecycleOwner: LifecycleOwner, eventBus: EventBus, uid: Int, name: String?) {
        App.get().trackAgent.post(BlackListTrackEvent(false, uid.toString(), name))
        lifecycleOwner.lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                BlackListBiz.getInstance().delDefaultBlackList(uid, name)
            }
            eventBus.postDefault(BlackListChangeEvent(uid, name, null, false))
        }
    }
}
