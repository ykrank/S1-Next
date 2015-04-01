package cl.monsoon.s1next;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.StrictMode;
import android.preference.PreferenceManager;

import com.bugsnag.android.Bugsnag;

import net.danlew.android.joda.JodaTimeAndroid;

import cl.monsoon.s1next.singleton.Setting;

public final class App extends Application {

    private static Context sContext;

    public static Context getContext() {
        return sContext;
    }

    @Override
    public void onCreate() {
        // enable StrictMode when debug
        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(
                    new StrictMode.ThreadPolicy.Builder()
                            .detectAll()
                            .penaltyLog()
                            .build());
            StrictMode.setVmPolicy(
                    new StrictMode.VmPolicy.Builder()
                            .detectAll()
                            .penaltyLog()
                            .build());
        }

        sContext = getApplicationContext();

        Bugsnag.init(this);

        JodaTimeAndroid.init(this);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        // initiate the config depends on settings
        Setting.General.setTextScale(sharedPreferences);
        Setting.Theme.setCurrentTheme(sharedPreferences);
        Setting.Download.setAvatarCacheInvalidationInterval(sharedPreferences);
        Setting.Download.setAvatarsDownloadStrategy(sharedPreferences);
        Setting.Download.setAvatarResolutionStrategy(sharedPreferences);
        Setting.Download.setImagesDownloadStrategy(sharedPreferences);
    }
}
