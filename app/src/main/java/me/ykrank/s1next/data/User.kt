package me.ykrank.s1next.data

import android.text.TextUtils
import me.ykrank.s1next.data.pref.AppDataPreferencesManager

open class User(private val appDataPref: AppDataPreferencesManager) {

    @Volatile var uid: String? = null

    @Volatile var name: String? = null

    @Volatile var permission: Int = 0

    @Volatile var authenticityToken: String? = null

    var appSecureToken: String?
        get() = appDataPref.appToken
        set(value) {
            appDataPref.appToken = value
        }

    @Volatile open var isLogged: Boolean = false

    @Volatile open var isSigned: Boolean = false

    val isAppLogged: Boolean
        get() = !TextUtils.isEmpty(appSecureToken)

    val key: String
        get() {
            if (!TextUtils.isEmpty(uid)) {
                return uid!!
            }
            return "anonymous"
        }
}
