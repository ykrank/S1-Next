package me.ykrank.s1next.data.pref;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.preference.PreferenceManager;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import me.ykrank.s1next.data.Wifi;

/**
 * Created by ykrank on 2016/11/4.
 */
@Module
public class PrefModule {

    @Provides
    @Singleton
    SharedPreferences provideSharedPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Provides
    @Singleton
    NetworkPreferencesRepository provideNetworkPreferencesRepository(Context context, SharedPreferences sharedPreferences) {
        return new NetworkPreferencesRepository(context, sharedPreferences);
    }

    @Provides
    @Singleton
    NetworkPreferencesManager provideNetworkPreferencesManager(NetworkPreferencesRepository networkPreferencesRepository) {
        return new NetworkPreferencesManager(networkPreferencesRepository);
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
    ReadProgressPreferencesRepository provideReadProgressPreferencesProvider(Context context, SharedPreferences sharedPreferences, ObjectMapper objectMapper) {
        return new ReadProgressPreferencesRepository(context, sharedPreferences, objectMapper);
    }

    @Provides
    @Singleton
    ReadProgressPreferencesManager provideReadProgressPreferencesManager(ReadProgressPreferencesRepository readProgressPreferencesRepository) {
        return new ReadProgressPreferencesManager(readProgressPreferencesRepository);
    }

    @Provides
    @Singleton
    DataPreferencesRepository provideDataPreferencesProvider(Context context, SharedPreferences sharedPreferences) {
        return new DataPreferencesRepository(context, sharedPreferences);
    }

    @Provides
    @Singleton
    DataPreferencesManager provideDataPreferencesManager(DataPreferencesRepository preferencesRepository) {
        return new DataPreferencesManager(preferencesRepository);
    }
}
