package me.ykrank.s1next.data.api

import android.text.TextUtils
import com.github.ykrank.androidtools.util.L
import me.ykrank.s1next.App.Companion.get
import me.ykrank.s1next.data.User
import me.ykrank.s1next.data.api.app.model.AppLoginResult
import me.ykrank.s1next.data.api.app.model.AppUserInfo
import me.ykrank.s1next.data.api.model.Account
import me.ykrank.s1next.data.api.model.wrapper.BaseDataWrapper
import me.ykrank.s1next.data.api.model.wrapper.BaseResultWrapper
import me.ykrank.s1next.task.AutoSignTask

class UserValidator(private val mUser: User, private val mAutoSignTask: AutoSignTask) {
    /**
     * Intercepts the data in order to check whether current user's login status
     * has changed and update user's status if needed.
     *
     * @param d   The data we want to intercept.
     * @param <D> The data type.
     * @return Original data.
    </D> */
    fun <D> validateIntercept(d: D): D {
        var account: Account? = null
        if (d is BaseDataWrapper<*>) {
            account = (d as BaseDataWrapper<*>).data
        } else if (d is BaseResultWrapper<*>) {
            account = (d as BaseResultWrapper<*>).data
        }
        account?.let { validate(it) }
        return d
    }

    /**
     * Checks current user's login status and updates [User]'s in our app.
     */
    fun validate(account: Account) {
        val logged = mUser.isLogged
        val uid = account.uid
        if (INVALID_UID == uid || TextUtils.isEmpty(uid)) {
            if (logged) {
                // if account has expired
                mUser.uid = null
                mUser.name = null
                mUser.isLogged = false
                mUser.isSigned = false
            }
        } else {
            if (!logged) {
                // if account has logged
                mUser.uid = uid
                mUser.name = account.username
                mUser.isLogged = true
                mUser.isSigned = false
            }
        }
        mUser.permission = account.permission
        mUser.authenticityToken = account.authenticityToken
        if (mUser.isLogged) {
            L.setUser(mUser.uid, mUser.name)
            if (mAutoSignTask.lastCheck == 0L) {
                mAutoSignTask.silentCheck()
            }
        }
        get().trackAgent.setUser(mUser)
    }

    /**
     * validate user app signed info
     *
     * @return whether app signed
     */
    fun validateAppUserInfo(appUserInfo: AppUserInfo?): Boolean {
        if (appUserInfo == null) {
            return false
        }
        if (appUserInfo.isSigned != mUser.isSigned) {
            mUser.isSigned = appUserInfo.isSigned
            return true
        }
        return false
    }

    /**
     * Checks current user's app login info and updates [User]'s in our app
     *
     * @return whether app login info valid
     */
    fun validateAppLoginInfo(loginResult: AppLoginResult?): Boolean {
        if (loginResult == null) {
            return false
        }
        if (mUser.isLogged && TextUtils.equals(mUser.uid, loginResult.uid)) {
            mUser.appSecureToken = loginResult.secureToken
            return true
        }
        return false
    }

    companion object {
        private const val INVALID_UID = "0"
    }
}
