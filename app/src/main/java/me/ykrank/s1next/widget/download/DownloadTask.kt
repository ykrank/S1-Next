package me.ykrank.s1next.widget.download

import okhttp3.HttpUrl

/**
 * Created by ykrank on 7/12/24
 * 
 */
class DownloadTask(val httpUrl: HttpUrl) {
    val url: String
        get() = httpUrl.toString()
}