package cl.monsoon.s1next;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.os.StrictMode;

import com.bugsnag.android.Bugsnag;
import com.squareup.okhttp.OkHttpClient;

import javax.inject.Singleton;

import cl.monsoon.s1next.data.User;
import cl.monsoon.s1next.data.api.S1Service;
import cl.monsoon.s1next.data.pref.DownloadPreferencesManager;
import cl.monsoon.s1next.data.pref.GeneralPreferencesManager;
import cl.monsoon.s1next.data.pref.ThemeManager;
import cl.monsoon.s1next.util.ResourceUtil;
import cl.monsoon.s1next.view.activity.BaseActivity;
import cl.monsoon.s1next.view.activity.PostListActivity;
import cl.monsoon.s1next.view.adapter.ThreadListRecyclerViewAdapter;
import cl.monsoon.s1next.view.dialog.ThemeChangeDialogFragment;
import cl.monsoon.s1next.view.fragment.GeneralPreferenceFragment;
import cl.monsoon.s1next.viewmodel.UserViewModel;
import cl.monsoon.s1next.widget.EventBus;
import dagger.Component;

public final class App extends Application {

    private static App sApp;

    private GeneralPreferencesManager mGeneralPreferencesManager;

    private AppComponent mAppComponent;

    public static App get() {
        return sApp;
    }

    public static AppComponent getAppComponent(Context context) {
        return ((App) context.getApplicationContext()).getAppComponent();
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

        sApp = this;

        mAppComponent = DaggerApp_AppComponent.builder()
                .appModule(new AppModule(this))
                .build();
        mGeneralPreferencesManager = mAppComponent.getGeneralPreferencesManager();

        Bugsnag.init(this);

        // set scaling factor for fonts
        ResourceUtil.setScaledDensity(getResources(), mGeneralPreferencesManager.getTextScale());
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        ResourceUtil.setScaledDensity(getResources(), mGeneralPreferencesManager.getTextScale());
    }

    public AppComponent getAppComponent() {
        return mAppComponent;
    }

    /**
     * Indicates the class where this module is going to inject dependencies
     * or the dependencies we want to get.
     */
    @Singleton
    @Component(modules = AppModule.class)
    public interface AppComponent {

        S1Service getS1Service();

        OkHttpClient getOkHttpClient();

        EventBus getEventBus();

        GeneralPreferencesManager getGeneralPreferencesManager();

        DownloadPreferencesManager getDownloadPreferencesManager();

        ThemeManager getThemeManager();

        User getUser();

        UserViewModel getUserViewModel();

        void inject(BaseActivity activity);

        void inject(PostListActivity activity);

        void inject(GeneralPreferenceFragment fragment);

        void inject(ThemeChangeDialogFragment fragment);

        void inject(ThreadListRecyclerViewAdapter adapter);
    }
}
