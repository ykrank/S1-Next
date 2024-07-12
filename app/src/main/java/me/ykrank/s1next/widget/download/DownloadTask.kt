package me.ykrank.s1next.widget.download

import okhttp3.HttpUrl

/**
 * Created by yuanke on 7/12/24
 * @author yuanke.ykrank@bytedance.com
 */
class DownloadTask(val httpUrl: HttpUrl) {
    val url: String
        get() = httpUrl.toString()
}