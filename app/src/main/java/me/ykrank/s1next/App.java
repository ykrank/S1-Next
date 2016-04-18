package me.ykrank.s1next;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.os.StrictMode;

import com.activeandroid.ActiveAndroid;
import com.bugsnag.android.Bugsnag;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

import javax.inject.Singleton;

import dagger.Component;
import me.ykrank.s1next.data.User;
import me.ykrank.s1next.data.api.S1Service;
import me.ykrank.s1next.data.api.UserValidator;
import me.ykrank.s1next.data.pref.DownloadPreferencesManager;
import me.ykrank.s1next.data.pref.GeneralPreferencesManager;
import me.ykrank.s1next.data.pref.ReadProgressPreferencesManager;
import me.ykrank.s1next.data.pref.ThemeManager;
import me.ykrank.s1next.util.ResourceUtil;
import me.ykrank.s1next.view.activity.BaseActivity;
import me.ykrank.s1next.view.adapter.delegate.PostAdapterDelegate;
import me.ykrank.s1next.view.adapter.delegate.ThreadAdapterDelegate;
import me.ykrank.s1next.view.dialog.LogoutDialogFragment;
import me.ykrank.s1next.view.dialog.ThemeChangeDialogFragment;
import me.ykrank.s1next.view.fragment.PostListFragment;
import me.ykrank.s1next.view.fragment.PostListPagerFragment;
import me.ykrank.s1next.view.fragment.ReplyFragment;
import me.ykrank.s1next.view.fragment.setting.GeneralPreferenceFragment;
import me.ykrank.s1next.viewmodel.UserViewModel;
import me.ykrank.s1next.widget.AppActivityLifecycleCallbacks;
import me.ykrank.s1next.widget.EventBus;
import me.ykrank.s1next.widget.WifiBroadcastReceiver;
import okhttp3.OkHttpClient;

public final class App extends Application {
    public static final String LOG_TAG = "s1Next";
    
    private static App sApp;

    private GeneralPreferencesManager mGeneralPreferencesManager;

    private AppComponent mAppComponent;

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

    @Override
    public void onCreate() {
        super.onCreate();

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

        sApp = this;
        mAppComponent = DaggerApp_AppComponent.builder()
                .appModule(new AppModule(this))
                .build();
        mAppActivityLifecycleCallbacks = new AppActivityLifecycleCallbacks(this);
        registerActivityLifecycleCallbacks(mAppActivityLifecycleCallbacks);

        mGeneralPreferencesManager = mAppComponent.getGeneralPreferencesManager();
        // set scaling factor for fonts
        ResourceUtil.setScaledDensity(getResources(), mGeneralPreferencesManager.getFontScale());

        ActiveAndroid.initialize(this);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        ResourceUtil.setScaledDensity(getResources(), mGeneralPreferencesManager.getFontScale());
    }

    public boolean isAppVisible() {
        return mAppActivityLifecycleCallbacks.isAppVisible();
    }

    /**
     * Indicates the class where this module is going to inject dependencies
     * or the dependencies we want to get.
     */
    @Singleton
    @Component(modules = AppModule.class)
    public interface AppComponent {

        OkHttpClient getOkHttpClient();

        S1Service getS1Service();

        EventBus getEventBus();

        User getUser();

        UserValidator getUserValidator();

        UserViewModel getUserViewModel();

        GeneralPreferencesManager getGeneralPreferencesManager();

        DownloadPreferencesManager getDownloadPreferencesManager();

        ThemeManager getThemeManager();

        ReadProgressPreferencesManager getReadProgressPreferencesManager();

        void inject(BaseActivity activity);

        void inject(PostListFragment fragment);

        void inject(PostListPagerFragment fragment);

        void inject(ReplyFragment fragment);

        void inject(GeneralPreferenceFragment fragment);

        void inject(LogoutDialogFragment fragment);

        void inject(ThemeChangeDialogFragment fragment);

        void inject(ThreadAdapterDelegate delegate);

        void inject(PostAdapterDelegate delegate);

        void inject(WifiBroadcastReceiver wifiBroadcastReceiver);
    }
}
