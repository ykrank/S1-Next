package cl.monsoon.s1next;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.StrictMode;
import android.preference.PreferenceManager;

import com.bugsnag.android.Bugsnag;

import cl.monsoon.s1next.singleton.Settings;
import cl.monsoon.s1next.util.ResourceUtil;

public final class App extends Application {

    private static Context sContext;

    public static Context getContext() {
        return sContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();

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

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        // initiate the config depends on settings
        Settings.General.setTextScale(sharedPreferences);
        ResourceUtil.setScaledDensity(getResources(), Settings.General.getTextScale());
        Settings.General.setSignature(sharedPreferences);
        Settings.Theme.setCurrentTheme(sharedPreferences);
        Settings.Download.setAvatarCacheInvalidationInterval(sharedPreferences);
        Settings.Download.setAvatarsDownloadStrategy(sharedPreferences);
        Settings.Download.setAvatarResolutionStrategy(sharedPreferences);
        Settings.Download.setImagesDownloadStrategy(sharedPreferences);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        ResourceUtil.setScaledDensity(getResources(), Settings.General.getTextScale());
    }
}
