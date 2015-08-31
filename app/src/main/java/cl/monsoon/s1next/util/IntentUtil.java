package cl.monsoon.s1next.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Parcelable;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cl.monsoon.s1next.R;
import cl.monsoon.s1next.view.internal.CoordinatorLayoutAnchorDelegate;

public final class IntentUtil {

    /**
     * This activity is displayed when the system attempts to start an Intent for
     * which there is more than one matching activity.
     * <p>
     * https://github.com/android/platform_frameworks_base/blob/master/core/java/com/android/internal/app/ResolverActivity.java
     */
    private static final String ANDROID_RESOLVER_ACTIVITY = "com.android.internal.app.ResolverActivity";

    private IntentUtil() {}

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
                    && !packageName.equals(ourAppPackageName)) {
                intent.setClassName(packageName, activityInfo.name);
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
            Intent chooserIntent = Intent.createChooser(targetIntentList.remove(0),
                    context.getString(R.string.intent_title_which_view_application));
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, targetIntentList.toArray(
                    new Parcelable[targetIntentList.size()]));
            context.startActivity(chooserIntent);
        }
    }
}
