package me.ykrank.s1next.util

import android.content.Context
import android.os.Build
import com.github.ykrank.androidtools.util.StringUtils
import me.ykrank.s1next.App
import me.ykrank.s1next.R

object AppDeviceUtil {

    val packageInfo by lazy {
        App.get().packageManager.getPackageInfo(packageName, 0)
    }

    val packageName
        get() = App.get().packageName

    @JvmStatic
    fun getVersionName(): String {
        return packageInfo.versionName
    }

    fun getAppFullVersionName(): String {
        return getVersionName()
    }

    fun getAppDownloadUrl(): String {
        return if (packageName.contains("alpha")) {
            "https://www.pgyer.com/xfPejhuq"
        } else {
            "https://www.pgyer.com/GcUxKd4w"
        }
    }

    /**
     * Gets the string signature which is used for reply (show in setting).
     */
    fun getSignature(context: Context): String {
        return context.getString(
            R.string.signature,
            getAppDownloadUrl(),
            getAppFullVersionName()
        )
    }

    /**
     * Gets the string signature with device info which is used for reply (append this to last line of the reply).
     */
    fun getSignatureWithDeviceInfo(context: Context): String {
        return context.getString(
            R.string.signature_with_device_info,
            deviceNameWithVersion,
            getAppDownloadUrl(),
            getAppFullVersionName()
        )
    }

    /**
     * Gets the string signature which is used for reply (append this to last line of the reply).
     */
    fun getPostSignature(context: Context): String {
        return context.getString(
            R.string.signature_in_reply,
            getAppDownloadUrl(),
            getAppFullVersionName()
        )
    }

    /**
     * Gets the string signature with device info which is used for reply (append this to last line of the reply).
     */
    fun getPostSignatureWithDeviceInfo(context: Context): String {
        return context.getString(
            R.string.signature_in_reply_with_device_info,
            deviceNameWithVersion,
            getAppDownloadUrl(),
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
