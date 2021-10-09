package com.github.ykrank.androidtools.extension

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

fun Int.dp2px(context: Context): Int {
    return (context.resources.displayMetrics.density * this + 0.5f).toInt()
}

fun Int.px2dp(context: Context): Int {
    return (this / context.resources.displayMetrics.density).toInt()
}

fun Int.sp2px(context: Context): Int {
    return (context.resources.displayMetrics.scaledDensity * this + 0.5f).toInt()
}