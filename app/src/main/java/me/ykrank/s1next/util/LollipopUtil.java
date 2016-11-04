package me.ykrank.s1next.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.content.ContextCompat;

import me.ykrank.s1next.App;
import me.ykrank.s1next.R;
import me.ykrank.s1next.data.pref.ThemeManager;

final class LollipopUtil {

    private LollipopUtil() {}

    /**
     * Changes app title color to white in recent apps.
     * <p>
     * See http://stackoverflow.com/a/27703150
     */
    @SuppressWarnings("unused")
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void changeAppTitleColorToWhiteInRecentAppsForInverseTheme(Activity activity) {
        int colorId;
        ThemeManager.Theme currentTheme = App.getPrefComponent(activity).getThemeManager().getTheme();
        // We can't find any similar color to change app title to
        // white for ThemeManager.Theme.LIGHT_THEME_INVERSE_AMBER.
        if (currentTheme == ThemeManager.Theme.LIGHT_THEME_INVERSE_GREEN) {
            colorId = R.color.green_600;
        } else if (currentTheme == ThemeManager.Theme.LIGHT_THEME_INVERSE_LIGHT_BLUE) {
            colorId = R.color.light_blue_600;
        } else {
            return;
        }

        ActivityManager.TaskDescription taskDescription = new ActivityManager.TaskDescription(
                activity.getString(R.string.app_name),
                BitmapFactory.decodeResource(activity.getResources(), R.mipmap.ic_launcher),
                ContextCompat.getColor(activity, colorId));
        activity.setTaskDescription(taskDescription);
    }
}
