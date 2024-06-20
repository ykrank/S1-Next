package me.ykrank.s1next.util

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.widget.Toast
import androidx.core.app.BundleCompat
import com.github.ykrank.androidtools.ui.internal.CoordinatorLayoutAnchorDelegate
import me.ykrank.s1next.R

object IntentUtil {
    const val EXTRA_ENABLE_URLBAR_HIDING = "android.support.customtabs.extra.ENABLE_URLBAR_HIDING"

    /**
     * This activity is displayed when the system attempts to start an Intent for
     * which there is more than one matching activity.
     *
     *
     * https://github.com/android/platform_frameworks_base/blob/master/core/java/com/android/internal/app/ResolverActivity.java
     */
    private const val ANDROID_RESOLVER_ACTIVITY = "com.android.internal.app.ResolverActivity"

    /**
     * HuaWei emui change this class
     */
    private const val ANDROID_RESOLVER_ACTIVITY_HUAWEI =
        "com.huawei.android.internal.app.HwResolverActivity"

    /**
     * Lenovo ZUK change this class
     */
    private const val ANDROID_RESOLVER_ACTIVITY_ZUK = "com.zui.resolver.ResolverActivity"

    /**
     * see https://github.com/GoogleChrome/custom-tabs-client/blob/master/customtabs/src/android/support/customtabs/CustomTabsIntent.java
     */
    private const val EXTRA_CUSTOM_TABS_SESSION = "android.support.customtabs.extra.SESSION"

    /**
     * Opens a uri in Android's web browser or other app which can handle this Intent.
     *
     * @param uri The Uri of the data the intent is targeting.
     */
    @JvmStatic
    fun startViewIntentExcludeOurApp(context: Context, uri: Uri?) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setData(uri)

        // find the default Intent except our app
        val defaultResolveInfo = context.packageManager.resolveActivity(
            intent,
            PackageManager.MATCH_DEFAULT_ONLY
        )
        val ourAppPackageName = context.packageName
        if (defaultResolveInfo != null) {
            val activityInfo = defaultResolveInfo.activityInfo
            val packageName = activityInfo.applicationInfo.packageName
            // if this is not the default resolver Activity or our app
            if (activityInfo.name != ANDROID_RESOLVER_ACTIVITY
                && activityInfo.name != ANDROID_RESOLVER_ACTIVITY_HUAWEI
                && activityInfo.name != ANDROID_RESOLVER_ACTIVITY_ZUK
                && packageName != ourAppPackageName
            ) {
                intent.setClassName(packageName, activityInfo.name)
                putCustomTabsExtra(intent)
                context.startActivity(intent)
                return
            }
        }

        // find all target Intents except our app if we don't find the default Intent
        val resolveInfoList = context.packageManager.queryIntentActivities(
            intent, 0
        )
        val targetIntentList: MutableList<Intent> = ArrayList(resolveInfoList.size)
        for (resolveInfo in resolveInfoList) {
            val packageName = resolveInfo.activityInfo.applicationInfo.packageName
            if (packageName != ourAppPackageName) {
                val targetIntent = Intent(Intent.ACTION_VIEW)
                targetIntent.setData(uri)
                targetIntent.setPackage(packageName)
                targetIntentList.add(targetIntent)
            }
        }
        if (targetIntentList.isEmpty()) {
            if (context is CoordinatorLayoutAnchorDelegate) {
                (context as CoordinatorLayoutAnchorDelegate).showShortSnackbar(
                    R.string.message_chooser_no_applications
                )
            } else {
                Toast.makeText(
                    context, R.string.message_chooser_no_applications,
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            putCustomTabsExtra(targetIntentList)
            val chooserIntent = Intent.createChooser(
                targetIntentList.removeAt(0),
                context.getString(R.string.intent_title_which_view_application)
            )
            chooserIntent.putExtra(
                Intent.EXTRA_INITIAL_INTENTS,
                targetIntentList.toTypedArray<Parcelable>()
            )
            context.startActivity(chooserIntent)
        }
    }

    @JvmStatic
    fun putCustomTabsExtra(intent: Intent) {
        // enable Custom Tabs if supported
        val bundle = Bundle()
        BundleCompat.putBinder(bundle, EXTRA_CUSTOM_TABS_SESSION, null)
        intent.putExtras(bundle)
        intent.putExtra(EXTRA_ENABLE_URLBAR_HIDING, true)
    }

    private fun putCustomTabsExtra(intentList: List<Intent>) {
        // enable Custom Tabs if supported
        val bundle = Bundle()
        BundleCompat.putBinder(bundle, EXTRA_CUSTOM_TABS_SESSION, null)
        for (intent in intentList) {
            intent.putExtras(bundle)
            intent.putExtra(EXTRA_ENABLE_URLBAR_HIDING, true)
        }
    }

    val REGEX_MATCH_ALL_PATH = ".*".toRegex()

    fun matchMainHost(host: String, mainHost: String): Boolean {
        return host == mainHost || host.endsWith(".${mainHost}")
    }

    fun matchPath(path: String, pathPattern: Regex): Boolean {
        return pathPattern == REGEX_MATCH_ALL_PATH || path.matches(pathPattern)
    }
}
