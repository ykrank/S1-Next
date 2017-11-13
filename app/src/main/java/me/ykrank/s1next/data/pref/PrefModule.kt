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
class PrefModule(private val prefContext: Context) {

    @Provides
    @Singleton
    internal fun provideSharedPreferences(): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(prefContext)
    }

    @Provides
    @Singleton
    internal fun provideNetworkPreferencesRepository(sharedPreferences: SharedPreferences): NetworkPreferences {
        return NetworkPreferencesImpl(prefContext, sharedPreferences)
    }

    @Provides
    @Singleton
    internal fun provideNetworkPreferencesManager(networkPreferences: NetworkPreferences): NetworkPreferencesManager {
        return NetworkPreferencesManager(networkPreferences)
    }

    @Provides
    @Singleton
    internal fun provideGeneralPreferencesProvider(sharedPreferences: SharedPreferences): GeneralPreferences {
        return GeneralPreferencesImpl(prefContext, sharedPreferences)
    }

    @Provides
    @Singleton
    internal fun provideGeneralPreferencesManager(generalPreferencesProvider: GeneralPreferences): GeneralPreferencesManager {
        return GeneralPreferencesManager(generalPreferencesProvider)
    }

    @Provides
    @Singleton
    internal fun provideThemeManager(generalPreferencesProvider: GeneralPreferences): ThemeManager {
        return ThemeManager(prefContext, generalPreferencesProvider)
    }

    @Provides
    @Singleton
    internal fun provideDownloadPreferencesProvider(sharedPreferences: SharedPreferences): DownloadPreferences {
        return DownloadPreferencesImpl(prefContext, sharedPreferences)
    }

    @Provides
    @Singleton
    internal fun provideDownloadPreferencesManager(downloadPreferencesProvider: DownloadPreferences, wifi: Wifi): DownloadPreferencesManager {
        return DownloadPreferencesManager(downloadPreferencesProvider, wifi)
    }

    @Provides
    @Singleton
    internal fun provideReadProgressPreferencesProvider(sharedPreferences: SharedPreferences, objectMapper: ObjectMapper): ReadProgressPreferences {
        return ReadProgressPreferencesImpl(prefContext, sharedPreferences, objectMapper)
    }

    @Provides
    @Singleton
    internal fun provideReadProgressPreferencesManager(readProgressPreferences: ReadProgressPreferences): ReadProgressPreferencesManager {
        return ReadProgressPreferencesManager(readProgressPreferences)
    }

    @Provides
    @Singleton
    internal fun provideDataPreferencesProvider(sharedPreferences: SharedPreferences): DataPreferences {
        return DataPreferencesImpl(prefContext, sharedPreferences)
    }

    @Provides
    @Singleton
    internal fun provideDataPreferencesManager(preferencesRepository: DataPreferences): DataPreferencesManager {
        return DataPreferencesManager(preferencesRepository)
    }

    @Provides
    @Singleton
    internal fun provideAppDataPreferencesProvider(sharedPreferences: SharedPreferences): AppDataPreferences {
        return AppDataPreferencesImpl(prefContext, sharedPreferences)
    }

    @Provides
    @Singleton
    internal fun provideAppDataPreferencesManager(preferencesRepository: AppDataPreferences): AppDataPreferencesManager {
        return AppDataPreferencesManager(preferencesRepository)
    }
}
