package cl.monsoon.s1next.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.graphics.BitmapFactory;
import android.os.Build;

import cl.monsoon.s1next.R;
import cl.monsoon.s1next.singleton.Settings;

final class VersionUtil {

    private VersionUtil() {

    }

    /**
     * Change app title color to white in recent apps.
     * <p>
     * See https://stackoverflow.com/questions/26899820/android-5-0-how-to-change-recent-apps-title-color#answer-27703150
     */
    @SuppressWarnings("unused")
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void changeAppTitleColorToWhiteInRecentApps(Activity activity) {
        int colorId;
        switch (Settings.Theme.getCurrentTheme()) {
            // We can't find any similar color to change app title to
            // white for Settings.LIGHT_THEME_AMBER.
            case Settings.Theme.LIGHT_THEME_INVERSE_GREEN:
                colorId = R.color.green_600;

                break;
            case Settings.Theme.LIGHT_THEME_INVERSE_LIGHT_BLUE:
                colorId = R.color.light_blue_600;

                break;
            default:
                colorId = -1;
        }

        if (colorId == -1) {
            return;
        }

        ActivityManager.TaskDescription taskDescription = new ActivityManager.TaskDescription(
                activity.getString(R.string.app_name),
                BitmapFactory.decodeResource(activity.getResources(), R.mipmap.ic_launcher),
                activity.getResources().getColor(colorId));
        activity.setTaskDescription(taskDescription);
    }
}
