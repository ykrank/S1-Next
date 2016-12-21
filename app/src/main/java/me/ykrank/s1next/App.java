package me.ykrank.s1next;

import android.content.Context;
import android.content.res.Configuration;
import android.os.StrictMode;
import android.support.multidex.MultiDexApplication;

import com.activeandroid.ActiveAndroid;
import com.bugsnag.android.Bugsnag;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

import me.ykrank.s1next.data.pref.DaggerPrefComponent;
import me.ykrank.s1next.data.pref.GeneralPreferencesManager;
import me.ykrank.s1next.data.pref.PrefComponent;
import me.ykrank.s1next.data.pref.PrefModule;
import me.ykrank.s1next.util.ProcessUtil;
import me.ykrank.s1next.util.ResourceUtil;
import me.ykrank.s1next.widget.AppActivityLifecycleCallbacks;

public final class App extends MultiDexApplication {
    public static final String LOG_TAG = "s1Next";
    
    private static App sApp;

    private GeneralPreferencesManager mGeneralPreferencesManager;

    private AppComponent mAppComponent;
    
    private PrefComponent mPrefComponent;

    private AppActivityLifecycleCallbacks mAppActivityLifecycleCallbacks;

    private RefWatcher refWatcher;

    public static App get() {
        return sApp;
    }

    public RefWatcher getRefWatcher() {
        return refWatcher;
    }

    public static AppComponent getAppComponent(Context context) {
        return ((App) context.getApplicationContext()).mAppComponent;
    }

    public static PrefComponent getPrefComponent(Context context) {
        return ((App) context.getApplicationContext()).mPrefComponent;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }

        // enable StrictMode when debug
        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .build());
        }
        refWatcher = LeakCanary.install(this);
        Bugsnag.init(this);

        //如果不是主进程，不做多余的初始化
        if (!ProcessUtil.isMainProcess(this))
            return;

        sApp = this;
        mAppComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .build();
        mPrefComponent = DaggerPrefComponent.builder()
                .appComponent(mAppComponent)
                .prefModule(new PrefModule())
                .build();
        mAppActivityLifecycleCallbacks = new AppActivityLifecycleCallbacks(this);
        registerActivityLifecycleCallbacks(mAppActivityLifecycleCallbacks);

        mGeneralPreferencesManager = mPrefComponent.getGeneralPreferencesManager();
        // set scaling factor for fonts
        ResourceUtil.setScaledDensity(getResources(), mGeneralPreferencesManager.getFontScale());

        ActiveAndroid.initialize(this);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        //如果不是主进程，不做多余的初始化
        if (!ProcessUtil.isMainProcess(this))
            return;
        
        ResourceUtil.setScaledDensity(getResources(), mGeneralPreferencesManager.getFontScale());
    }

    public boolean isAppVisible() {
        return mAppActivityLifecycleCallbacks.isAppVisible();
    }

}
