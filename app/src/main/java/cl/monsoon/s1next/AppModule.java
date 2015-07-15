package cl.monsoon.s1next;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import javax.inject.Singleton;

import cl.monsoon.s1next.data.User;
import cl.monsoon.s1next.data.Wifi;
import cl.monsoon.s1next.data.pref.DownloadPreferencesManager;
import cl.monsoon.s1next.data.pref.DownloadPreferencesRepository;
import cl.monsoon.s1next.data.pref.GeneralPreferencesManager;
import cl.monsoon.s1next.data.pref.GeneralPreferencesRepository;
import cl.monsoon.s1next.data.pref.ThemeManager;
import cl.monsoon.s1next.viewmodel.UserViewModel;
import dagger.Module;
import dagger.Provides;

@Module
public final class AppModule {

    private final Application mApplication;

    public AppModule(Application application) {
        this.mApplication = application;
    }

    @Provides
    @Singleton
    Context provideContext() {
        return mApplication;
    }

    @Provides
    @Singleton
    SharedPreferences provideSharedPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(mApplication);
    }

    @Provides
    @Singleton
    GeneralPreferencesRepository provideGeneralPreferencesProvider(Context context, SharedPreferences sharedPreferences) {
        return new GeneralPreferencesRepository(context, sharedPreferences);
    }

    @Provides
    @Singleton
    GeneralPreferencesManager provideGeneralPreferencesManager(GeneralPreferencesRepository generalPreferencesProvider) {
        return new GeneralPreferencesManager(generalPreferencesProvider);
    }

    @Provides
    @Singleton
    ThemeManager provideThemeManager(Context context, GeneralPreferencesRepository generalPreferencesProvider) {
        return new ThemeManager(context, generalPreferencesProvider);
    }

    @Provides
    @Singleton
    DownloadPreferencesRepository provideDownloadPreferencesProvider(Context context, SharedPreferences sharedPreferences) {
        return new DownloadPreferencesRepository(context, sharedPreferences);
    }

    @Provides
    @Singleton
    DownloadPreferencesManager provideDownloadPreferencesManager(DownloadPreferencesRepository downloadPreferencesProvider, Wifi wifi) {
        return new DownloadPreferencesManager(downloadPreferencesProvider, wifi);
    }

    @Provides
    @Singleton
    User providerUser(UserViewModel userViewModel) {
        return userViewModel.getUser();
    }

    @Provides
    @Singleton
    UserViewModel providerUserViewModel() {
        return new UserViewModel();
    }

    @Provides
    @Singleton
    Wifi providerWifi() {
        return new Wifi();
    }
}
