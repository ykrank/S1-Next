package cl.monsoon.s1next;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.os.StrictMode;

import com.bugsnag.android.Bugsnag;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.okhttp.OkHttpClient;

import javax.inject.Singleton;

import cl.monsoon.s1next.data.User;
import cl.monsoon.s1next.data.api.S1Service;
import cl.monsoon.s1next.data.api.UserValidator;
import cl.monsoon.s1next.data.pref.DownloadPreferencesManager;
import cl.monsoon.s1next.data.pref.GeneralPreferencesManager;
import cl.monsoon.s1next.data.pref.ThemeManager;
import cl.monsoon.s1next.util.ResourceUtil;
import cl.monsoon.s1next.view.activity.BaseActivity;
import cl.monsoon.s1next.view.adapter.delegate.PostAdapterDelegate;
import cl.monsoon.s1next.view.adapter.delegate.ThreadAdapterDelegate;
import cl.monsoon.s1next.view.dialog.LogoutDialogFragment;
import cl.monsoon.s1next.view.dialog.ThemeChangeDialogFragment;
import cl.monsoon.s1next.view.fragment.GeneralPreferenceFragment;
import cl.monsoon.s1next.view.fragment.PostListFragment;
import cl.monsoon.s1next.view.fragment.ReplyFragment;
import cl.monsoon.s1next.viewmodel.UserViewModel;
import cl.monsoon.s1next.widget.AppActivityLifecycleCallbacks;
import cl.monsoon.s1next.widget.EventBus;
import dagger.Component;

public final class App extends Application {

    private static App sApp;

    private GeneralPreferencesManager mGeneralPreferencesManager;

    private AppComponent mAppComponent;

    private AppActivityLifecycleCallbacks mAppActivityLifecycleCallbacks;

    public static App get() {
        return sApp;
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
        LeakCanary.install(this);
        Bugsnag.init(this);

        sApp = this;
        mAppActivityLifecycleCallbacks = new AppActivityLifecycleCallbacks();
        registerActivityLifecycleCallbacks(mAppActivityLifecycleCallbacks);

        mAppComponent = DaggerApp_AppComponent.builder()
                .appModule(new AppModule(this))
                .build();
        mGeneralPreferencesManager = mAppComponent.getGeneralPreferencesManager();
        // set scaling factor for fonts
        ResourceUtil.setScaledDensity(getResources(), mGeneralPreferencesManager.getFontScale());
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

        void inject(BaseActivity activity);

        void inject(PostListFragment fragment);

        void inject(ReplyFragment fragment);

        void inject(GeneralPreferenceFragment fragment);

        void inject(LogoutDialogFragment fragment);

        void inject(ThemeChangeDialogFragment fragment);

        void inject(ThreadAdapterDelegate delegate);

        void inject(PostAdapterDelegate delegate);
    }
}
