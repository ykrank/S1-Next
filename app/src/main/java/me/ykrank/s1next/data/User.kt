package me.ykrank.s1next.data

import android.text.TextUtils

open class User {

    @Volatile var uid: String? = null

    @Volatile var name: String? = null

    @Volatile var permission: Int = 0

    @Volatile var authenticityToken: String? = null

    @Volatile open var isLogged: Boolean = false

    @Volatile open var isSigned: Boolean = false

    val key: String
        get() {
            if (!TextUtils.isEmpty(uid)) {
                return uid!!
            }
            return "anonymous"
        }
}
