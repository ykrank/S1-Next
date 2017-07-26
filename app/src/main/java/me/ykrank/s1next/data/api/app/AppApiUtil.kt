package me.ykrank.s1next.data.api.app

import android.support.v4.app.FragmentActivity
import me.ykrank.s1next.data.User
import me.ykrank.s1next.data.api.ApiException
import me.ykrank.s1next.view.dialog.LoginPromptDialogFragment

/**
 * Created by ykrank on 2017/7/25.
 */
object AppApiUtil {

    fun appLoginIfNeed(activity: FragmentActivity, user: User, throwable: Throwable): Boolean {
        if (throwable is ApiException.AppServerException) {
            if (throwable.code == 503) {
//                user.appSecureToken = null
                LoginPromptDialogFragment.showAppLoginPromptDialogIfNeeded(activity, user)
                return true
            }
        }

        return false
    }
}