package me.ykrank.s1next.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.BundleCompat;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import me.ykrank.s1next.R;
import me.ykrank.s1next.view.internal.CoordinatorLayoutAnchorDelegate;

public final class IntentUtil {

    public static final String EXTRA_ENABLE_URLBAR_HIDING = "android.support.customtabs.extra.ENABLE_URLBAR_HIDING";
    /**
     * This activity is displayed when the system attempts to start an Intent for
     * which there is more than one matching activity.
     * <p>
     * https://github.com/android/platform_frameworks_base/blob/master/core/java/com/android/internal/app/ResolverActivity.java
     */
    private static final String ANDROID_RESOLVER_ACTIVITY = "com.android.internal.app.ResolverActivity";
    /**
     * HuaWei emui change this class
     */
    private static final String ANDROID_RESOLVER_ACTIVITY_HUAWEI = "com.huawei.android.internal.app.HwResolverActivity";
    /**
     * see https://github.com/GoogleChrome/custom-tabs-client/blob/master/customtabs/src/android/support/customtabs/CustomTabsIntent.java
     */
    private static final String EXTRA_CUSTOM_TABS_SESSION = "android.support.customtabs.extra.SESSION";

    private IntentUtil() {
    }

    /**
     * Opens a uri in Android's web browser or other app which can handle this Intent.
     *
     * @param uri The Uri of the data the intent is targeting.
     */
    public static void startViewIntentExcludeOurApp(Context context, Uri uri) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(uri);

        // find the default Intent except our app
        ResolveInfo defaultResolveInfo = context.getPackageManager().resolveActivity(intent,
                PackageManager.MATCH_DEFAULT_ONLY);
        final String ourAppPackageName = context.getPackageName();
        if (defaultResolveInfo != null) {
            ActivityInfo activityInfo = defaultResolveInfo.activityInfo;
            String packageName = activityInfo.applicationInfo.packageName;
            // if this is not the default resolver Activity or our app
            if (!activityInfo.name.equals(ANDROID_RESOLVER_ACTIVITY)
                    && !activityInfo.name.equals(ANDROID_RESOLVER_ACTIVITY_HUAWEI)
                    && !packageName.equals(ourAppPackageName)) {
                intent.setClassName(packageName, activityInfo.name);
                putCustomTabsExtra(intent);
                context.startActivity(intent);

                return;
            }
        }

        // find all target Intents except our app if we don't find the default Intent
        List<ResolveInfo> resolveInfoList = context.getPackageManager().queryIntentActivities(
                intent, 0);
        List<Intent> targetIntentList = new ArrayList<>(resolveInfoList.size());
        for (ResolveInfo resolveInfo : resolveInfoList) {
            String packageName = resolveInfo.activityInfo.applicationInfo.packageName;
            if (!packageName.equals(ourAppPackageName)) {
                Intent targetIntent = new Intent(Intent.ACTION_VIEW);
                targetIntent.setData(uri);
                targetIntent.setPackage(packageName);
                targetIntentList.add(targetIntent);
            }
        }

        if (targetIntentList.isEmpty()) {
            if (context instanceof CoordinatorLayoutAnchorDelegate) {
                ((CoordinatorLayoutAnchorDelegate) context).showShortSnackbar(
                        R.string.message_chooser_no_applications);
            } else {
                Toast.makeText(context, R.string.message_chooser_no_applications,
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            putCustomTabsExtra(targetIntentList);
            Intent chooserIntent = Intent.createChooser(targetIntentList.remove(0),
                    context.getString(R.string.intent_title_which_view_application));
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, targetIntentList.toArray(
                    new Parcelable[targetIntentList.size()]));
            context.startActivity(chooserIntent);
        }
    }

    public static void putCustomTabsExtra(Intent intent) {
        // enable Custom Tabs if supported
        Bundle bundle = new Bundle();
        BundleCompat.putBinder(bundle, EXTRA_CUSTOM_TABS_SESSION, null);
        intent.putExtras(bundle);
        intent.putExtra(EXTRA_ENABLE_URLBAR_HIDING, true);
    }

    private static void putCustomTabsExtra(List<Intent> intentList) {
        // enable Custom Tabs if supported
        Bundle bundle = new Bundle();
        BundleCompat.putBinder(bundle, EXTRA_CUSTOM_TABS_SESSION, null);
        for (Intent intent : intentList) {
            intent.putExtras(bundle);
            intent.putExtra(EXTRA_ENABLE_URLBAR_HIDING, true);
        }
    }
}
