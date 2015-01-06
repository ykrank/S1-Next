package cl.monsoon.s1next;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.StrictMode;
import android.preference.PreferenceManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;

import java.io.InputStream;

import cl.monsoon.s1next.singleton.Config;
import cl.monsoon.s1next.singleton.MyOkHttpClient;
import cl.monsoon.s1next.widget.OkHttpUrlLoader;

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

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        // initiate the config depends on settings
        Config.setCurrentTheme(sharedPreferences);
        Config.setTextScale(sharedPreferences);
        Config.setAvatarsDownloadStrategy(sharedPreferences);
        Config.setImagesDownloadStrategy(sharedPreferences);

        // register the OkHttp for Glide
        Glide.get(this).register(
                GlideUrl.class,
                InputStream.class,
                new OkHttpUrlLoader.Factory(MyOkHttpClient.get()));
    }
}
