package cl.monsoon.s1next.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;

import cl.monsoon.s1next.R;
import cl.monsoon.s1next.singleton.Config;

public final class VersionUtil {

    private VersionUtil() {

    }

    /**
     * Change app title color to white in recent apps.
     * <p>
     * See https://stackoverflow.com/questions/26899820/android-5-0-how-to-change-recent-apps-title-color#answer-27703150
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void changeAppTitleColorToWhiteInRecentApps(Activity activity) {
        int colorId;
        switch (Config.getCurrentTheme()) {
            // We can't find any similar color to change app title
            // to white for Config.LIGHT_THEME_AMBER.
            // And we also don't need to provide any color to Config.DARK_THEME
            // because the app title is already white in this theme.
            case Config.LIGHT_THEME_GREEN:
                colorId = R.color.green_600;

                break;
            case Config.LIGHT_THEME_LIGHT_BLUE:
                colorId = R.color.light_blue_600;

                break;
            default:
                colorId = -1;
        }

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
