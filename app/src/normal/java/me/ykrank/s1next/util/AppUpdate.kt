package me.ykrank.s1next.util

import android.content.Context
import android.content.Intent
import android.net.Uri


/**
 * Created by ykrank on 5/30/24
 * 
 */
object AppUpdate {

    fun checkUpdate(context: Context) {
        val url = AppDeviceUtil.getAppDownloadUrl()
        val uri = Uri.parse(url)
        val intent = Intent(Intent.ACTION_VIEW, uri)
        context.startActivity(intent)
    }
}