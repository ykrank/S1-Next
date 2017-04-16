package me.ykrank.s1next.data.pref;

import android.content.Context;
import android.content.SharedPreferences;

import com.fasterxml.jackson.databind.ObjectMapper;

import dagger.Module;
import dagger.Provides;
import me.ykrank.s1next.data.Wifi;

/**
 * Created by ykrank on 2016/11/4.
 */
@Module
public class PrefModule {

    @Provides
    @PrefScope
    GeneralPreferencesRepository provideGeneralPreferencesProvider(Context context, SharedPreferences sharedPreferences) {
        return new GeneralPreferencesRepository(context, sharedPreferences);
    }

    @Provides
    @PrefScope
    GeneralPreferencesManager provideGeneralPreferencesManager(GeneralPreferencesRepository generalPreferencesProvider) {
        return new GeneralPreferencesManager(generalPreferencesProvider);
    }

    @Provides
    @PrefScope
    ThemeManager provideThemeManager(Context context, GeneralPreferencesRepository generalPreferencesProvider) {
        return new ThemeManager(context, generalPreferencesProvider);
    }

    @Provides
    @PrefScope
    DownloadPreferencesRepository provideDownloadPreferencesProvider(Context context, SharedPreferences sharedPreferences) {
        return new DownloadPreferencesRepository(context, sharedPreferences);
    }

    @Provides
    @PrefScope
    DownloadPreferencesManager provideDownloadPreferencesManager(DownloadPreferencesRepository downloadPreferencesProvider, Wifi wifi) {
        return new DownloadPreferencesManager(downloadPreferencesProvider, wifi);
    }

    @Provides
    @PrefScope
    ReadProgressPreferencesRepository provideReadProgressPreferencesProvider(Context context, SharedPreferences sharedPreferences, ObjectMapper objectMapper) {
        return new ReadProgressPreferencesRepository(context, sharedPreferences, objectMapper);
    }

    @Provides
    @PrefScope
    ReadProgressPreferencesManager provideReadProgressPreferencesManager(ReadProgressPreferencesRepository readProgressPreferencesRepository) {
        return new ReadProgressPreferencesManager(readProgressPreferencesRepository);
    }

    @Provides
    @PrefScope
    DataPreferencesRepository provideDataPreferencesProvider(Context context, SharedPreferences sharedPreferences) {
        return new DataPreferencesRepository(context, sharedPreferences);
    }

    @Provides
    @PrefScope
    DataPreferencesManager provideDataPreferencesManager(DataPreferencesRepository preferencesRepository) {
        return new DataPreferencesManager(preferencesRepository);
    }
}
