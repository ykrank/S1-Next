package com.github.ykrank.androidtools.util

import android.net.Uri

/**
 * Created by ykrank on 9/5/24
 */
object UriUtil {


}

fun Uri.isNetwork(): Boolean {
    return scheme == "http" || scheme == "https"
}

fun Uri.isFile(): Boolean {
    return scheme == "file"
}