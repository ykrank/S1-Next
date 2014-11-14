package cl.monsoon.s1next;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.StrictMode;
import android.preference.PreferenceManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.integration.okhttp.OkHttpUrlLoader;
import com.bumptech.glide.load.model.GlideUrl;

import java.io.InputStream;

import cl.monsoon.s1next.fragment.SettingsFragment;
import cl.monsoon.s1next.singleton.MyOkHttpClient;

public final class MyApplication extends Application {

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

        // set theme depends on settings
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean nightMode =
                sharedPreferences.getBoolean(SettingsFragment.KEY_PREF_NIGHT_MODE, false);
        if (nightMode) {
            Config.setTheme(Config.DARK_THEME);
        } else {
            Config.setTheme(Config.LIGHT_THEME);
        }

        // init download strategy
        Config.setAvatarsDownloadStrategy(sharedPreferences);
        Config.setImagesDownloadStrategy(sharedPreferences);

        // register the OkHttp for Glide
        Glide.get(this).register(
                GlideUrl.class, InputStream.class, new OkHttpUrlLoader.Factory(MyOkHttpClient.get()));
    }
}
