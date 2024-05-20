package me.ykrank.s1next.util

import android.content.Context
import android.os.Build
import me.ykrank.s1next.BuildConfig
import me.ykrank.s1next.R
import org.apache.commons.lang3.StringUtils

object AppDeviceUtil {

    @JvmStatic
    fun getAppFullVersionName(): String {
        return BuildConfig.VERSION_NAME
    }

    /**
     * Gets the string signature which is used for reply (show in setting).
     */
    fun getSignature(context: Context): String {
        return context.getString(
            R.string.signature,
            deviceNameWithVersion,
            getAppFullVersionName()
        )
    }

    /**
     * Gets the string signature which is used for reply (append this to last line of the reply).
     */
    fun getPostSignature(context: Context): String {
        return context.getString(
            R.string.signature_in_reply,
            deviceNameWithVersion,
            getAppFullVersionName()
        )
    }

    /**
     * Forked from http://stackoverflow.com/a/12707479
     */
    private val deviceName: String by lazy {
        val manufacturer = Build.MANUFACTURER
        val model = Build.MODEL
        return@lazy if (model.startsWith(manufacturer)) {
            model
        } else {
            manufacturer + StringUtils.SPACE + model
        }
    }
    private val deviceNameWithVersion: String by lazy {
        deviceName + ',' + StringUtils.SPACE + "Android" + StringUtils.SPACE + Build.VERSION.RELEASE
    }
}
