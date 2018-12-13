package me.ykrank.s1next.data

import android.text.TextUtils
import com.github.ykrank.androidtools.data.TrackUser
import me.ykrank.s1next.data.pref.AppDataPreferencesManager

open class User(private val appDataPref: AppDataPreferencesManager) : TrackUser {

    @Volatile
    override var uid: String? = null

    @Volatile
    override var name: String? = null

    @Volatile
    override var permission: Int = 0

    override val extras: Map<String, String> = hashMapOf()

    @Volatile
    var authenticityToken: String? = null

    var appSecureToken: String?
        get() = appDataPref.appToken
        set(value) {
            appDataPref.appToken = value
        }

    @Volatile
    open var isLogged: Boolean = false

    @Volatile
    open var isSigned: Boolean = false

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
