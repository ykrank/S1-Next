package me.ykrank.s1next.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import me.ykrank.s1next.BuildConfig


/**
 * Created by yuanke on 5/30/24
 * @author yuanke.ykrank@bytedance.com
 */
object AppUpdate {

    fun checkUpdate(context: Context) {
        val url = if (BuildConfig.BUILD_TYPE == "alpha") {
            "https://www.pgyer.com/xfPejhuq"
        } else {
            "https://www.pgyer.com/xfPejhuq"
        }
        val uri = Uri.parse(url)
        val intent = Intent(Intent.ACTION_VIEW, uri)
        context.startActivity(intent)
    }
}