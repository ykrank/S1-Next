package me.ykrank.s1next.data.pref;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

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
    private final Context mContext;

    public PrefModule(Context mContext) {
        this.mContext = mContext;
    }

    @Provides
    @Singleton
    SharedPreferences provideSharedPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(mContext);
    }

    @Provides
    @Singleton
    NetworkPreferencesRepository provideNetworkPreferencesRepository(SharedPreferences sharedPreferences) {
        return new NetworkPreferencesRepository(mContext, sharedPreferences);
    }

    @Provides
    @Singleton
    NetworkPreferencesManager provideNetworkPreferencesManager(NetworkPreferencesRepository networkPreferencesRepository) {
        return new NetworkPreferencesManager(networkPreferencesRepository);
    }

    @Provides
    @Singleton
    GeneralPreferencesRepository provideGeneralPreferencesProvider(SharedPreferences sharedPreferences) {
        return new GeneralPreferencesRepository(mContext, sharedPreferences);
    }

    @Provides
    @Singleton
    GeneralPreferencesManager provideGeneralPreferencesManager(GeneralPreferencesRepository generalPreferencesProvider) {
        return new GeneralPreferencesManager(generalPreferencesProvider);
    }

    @Provides
    @Singleton
    ThemeManager provideThemeManager(GeneralPreferencesRepository generalPreferencesProvider) {
        return new ThemeManager(mContext, generalPreferencesProvider);
    }

    @Provides
    @Singleton
    DownloadPreferencesRepository provideDownloadPreferencesProvider(SharedPreferences sharedPreferences) {
        return new DownloadPreferencesRepository(mContext, sharedPreferences);
    }

    @Provides
    @Singleton
    DownloadPreferencesManager provideDownloadPreferencesManager(DownloadPreferencesRepository downloadPreferencesProvider, Wifi wifi) {
        return new DownloadPreferencesManager(downloadPreferencesProvider, wifi);
    }

    @Provides
    @Singleton
    ReadProgressPreferencesRepository provideReadProgressPreferencesProvider(SharedPreferences sharedPreferences, ObjectMapper objectMapper) {
        return new ReadProgressPreferencesRepository(mContext, sharedPreferences, objectMapper);
    }

    @Provides
    @Singleton
    ReadProgressPreferencesManager provideReadProgressPreferencesManager(ReadProgressPreferencesRepository readProgressPreferencesRepository) {
        return new ReadProgressPreferencesManager(readProgressPreferencesRepository);
    }

    @Provides
    @Singleton
    DataPreferencesRepository provideDataPreferencesProvider(SharedPreferences sharedPreferences) {
        return new DataPreferencesRepository(mContext, sharedPreferences);
    }

    @Provides
    @Singleton
    DataPreferencesManager provideDataPreferencesManager(DataPreferencesRepository preferencesRepository) {
        return new DataPreferencesManager(preferencesRepository);
    }
}
