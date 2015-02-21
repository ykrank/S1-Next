package cl.monsoon.s1next.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;

import cl.monsoon.s1next.R;
import cl.monsoon.s1next.singleton.Config;

public final class LollipopUtil {

    private LollipopUtil() {

    }

    /**
     * Change app title color in recent apps.
     * <p>
     * See https://stackoverflow.com/questions/26899820/android-5-0-how-to-change-recent-apps-title-color#answer-27703150
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void adjustTaskDescription(Activity activity) {
        int colorId = Config.getContrastColorForTaskDescription();
        if (colorId == -1) {
            return;
        }

        Bitmap bitmap = BitmapFactory.decodeResource(activity.getResources(), R.mipmap.ic_launcher);
        ActivityManager.TaskDescription taskDescription =
                new ActivityManager.TaskDescription(
                        activity.getString(R.string.app_name),
                        bitmap,
                        activity.getResources().getColor(colorId));
        activity.setTaskDescription(taskDescription);
    }
}
