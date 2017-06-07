package me.ykrank.s1next.data.pref

import android.content.Context
import android.content.SharedPreferences
import android.support.v7.preference.PreferenceManager
import com.fasterxml.jackson.databind.ObjectMapper
import dagger.Module
import dagger.Provides
import me.ykrank.s1next.data.Wifi
import javax.inject.Singleton

/**
 * Created by ykrank on 2016/11/4.
 */
@Module
class PrefModule {

    @Provides
    @Singleton
    internal fun provideSharedPreferences(context: Context): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(context)
    }

    @Provides
    @Singleton
    internal fun provideNetworkPreferencesRepository(context: Context, sharedPreferences: SharedPreferences): NetworkPreferences {
        return NetworkPreferencesImpl(context, sharedPreferences)
    }

    @Provides
    @Singleton
    internal fun provideNetworkPreferencesManager(networkPreferences: NetworkPreferences): NetworkPreferencesManager {
        return NetworkPreferencesManager(networkPreferences)
    }

    @Provides
    @Singleton
    internal fun provideGeneralPreferencesProvider(context: Context, sharedPreferences: SharedPreferences): GeneralPreferences {
        return GeneralPreferencesImpl(context, sharedPreferences)
    }

    @Provides
    @Singleton
    internal fun provideGeneralPreferencesManager(generalPreferencesProvider: GeneralPreferences): GeneralPreferencesManager {
        return GeneralPreferencesManager(generalPreferencesProvider)
    }

    @Provides
    @Singleton
    internal fun provideThemeManager(context: Context, generalPreferencesProvider: GeneralPreferences): ThemeManager {
        return ThemeManager(context, generalPreferencesProvider)
    }

    @Provides
    @Singleton
    internal fun provideDownloadPreferencesProvider(context: Context, sharedPreferences: SharedPreferences): DownloadPreferences {
        return DownloadPreferencesImpl(context, sharedPreferences)
    }

    @Provides
    @Singleton
    internal fun provideDownloadPreferencesManager(downloadPreferencesProvider: DownloadPreferences, wifi: Wifi): DownloadPreferencesManager {
        return DownloadPreferencesManager(downloadPreferencesProvider, wifi)
    }

    @Provides
    @Singleton
    internal fun provideReadProgressPreferencesProvider(context: Context, sharedPreferences: SharedPreferences, objectMapper: ObjectMapper): ReadProgressPreferences {
        return ReadProgressPreferencesImpl(context, sharedPreferences, objectMapper)
    }

    @Provides
    @Singleton
    internal fun provideReadProgressPreferencesManager(readProgressPreferences: ReadProgressPreferences): ReadProgressPreferencesManager {
        return ReadProgressPreferencesManager(readProgressPreferences)
    }

    @Provides
    @Singleton
    internal fun provideDataPreferencesProvider(context: Context, sharedPreferences: SharedPreferences): DataPreferences {
        return DataPreferencesImpl(context, sharedPreferences)
    }

    @Provides
    @Singleton
    internal fun provideDataPreferencesManager(preferencesRepository: DataPreferences): DataPreferencesManager {
        return DataPreferencesManager(preferencesRepository)
    }
}
