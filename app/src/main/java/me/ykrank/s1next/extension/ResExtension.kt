package me.ykrank.s1next.extension

import android.content.Context

/**
 * Extension res id to res
 * Created by ykrank on 2017/6/12.
 */
fun Int.resStr(context: Context): String {
    return context.getString(this)
}

fun Int.resBool(context: Context): Boolean {
    return context.resources.getBoolean(this)
}

fun Int.resInt(context: Context): Int {
    return context.resources.getInteger(this)
}