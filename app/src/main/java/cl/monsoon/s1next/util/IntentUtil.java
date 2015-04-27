package cl.monsoon.s1next.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Parcelable;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cl.monsoon.s1next.R;

public final class IntentUtil {

    private IntentUtil() {

    }

    public static void startViewIntentExcludeOurApp(Context context, Uri uri) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(uri);

        // find all target Intents except our app
        List<Intent> targetIntentList = new ArrayList<>();
        String ourAppPackageName = context.getPackageName();
        for (ResolveInfo resolveInfo : context.getPackageManager().queryIntentActivities(intent, 0)) {
            String packageName = resolveInfo.activityInfo.packageName;
            if (!packageName.equals(ourAppPackageName)) {
                Intent targetIntent = new Intent(Intent.ACTION_VIEW);
                targetIntent.setData(uri);
                targetIntent.setPackage(packageName);
                targetIntentList.add(targetIntent);
            }
        }

        if (targetIntentList.isEmpty()) {
            ToastUtil.showByResId(R.string.toast_message_chooser_no_applications, Toast.LENGTH_LONG);
        } else {
            Intent chooserIntent = Intent.createChooser(targetIntentList.remove(0),
                    context.getString(R.string.intent_title_which_view_application));
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, targetIntentList.toArray(
                    new Parcelable[targetIntentList.size()]));
            context.startActivity(chooserIntent);
        }
    }
}
