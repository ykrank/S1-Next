package me.ykrank.s1next.data.pref

import android.content.Context
import android.content.SharedPreferences
import android.support.v7.preference.PreferenceManager
import com.fasterxml.jackson.databind.ObjectMapper
import dagger.Module
import dagger.Provides
import me.ykrank.s1next.AppLife
import me.ykrank.s1next.data.Wifi

/**
 * Created by ykrank on 2016/11/4.
 */
@Module
class PrefModule {

    @Provides
    @AppLife
    internal fun provideSharedPreferences(context: Context): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(context)
    }

    @Provides
    @AppLife
    internal fun provideNetworkPreferencesRepository(context: Context, sharedPreferences: SharedPreferences): NetworkPreferences {
        return NetworkPreferencesImpl(context, sharedPreferences)
    }

    @Provides
    @AppLife
    internal fun provideNetworkPreferencesManager(networkPreferences: NetworkPreferences): NetworkPreferencesManager {
        return NetworkPreferencesManager(networkPreferences)
    }

    @Provides
    @AppLife
    internal fun provideGeneralPreferencesProvider(context: Context, sharedPreferences: SharedPreferences): GeneralPreferences {
        return GeneralPreferencesImpl(context, sharedPreferences)
    }

    @Provides
    @AppLife
    internal fun provideGeneralPreferencesManager(generalPreferencesProvider: GeneralPreferences): GeneralPreferencesManager {
        return GeneralPreferencesManager(generalPreferencesProvider)
    }

    @Provides
    @AppLife
    internal fun provideThemeManager(context: Context, generalPreferencesProvider: GeneralPreferences): ThemeManager {
        return ThemeManager(context, generalPreferencesProvider)
    }

    @Provides
    @AppLife
    internal fun provideDownloadPreferencesProvider(context: Context, sharedPreferences: SharedPreferences): DownloadPreferences {
        return DownloadPreferencesImpl(context, sharedPreferences)
    }

    @Provides
    @AppLife
    internal fun provideDownloadPreferencesManager(downloadPreferencesProvider: DownloadPreferences, wifi: Wifi): DownloadPreferencesManager {
        return DownloadPreferencesManager(downloadPreferencesProvider, wifi)
    }

    @Provides
    @AppLife
    internal fun provideReadProgressPreferencesProvider(context: Context, sharedPreferences: SharedPreferences, objectMapper: ObjectMapper): ReadProgressPreferences {
        return ReadProgressPreferencesImpl(context, sharedPreferences, objectMapper)
    }

    @Provides
    @AppLife
    internal fun provideReadProgressPreferencesManager(readProgressPreferences: ReadProgressPreferences): ReadProgressPreferencesManager {
        return ReadProgressPreferencesManager(readProgressPreferences)
    }

    @Provides
    @AppLife
    internal fun provideDataPreferencesProvider(context: Context, sharedPreferences: SharedPreferences): DataPreferences {
        return DataPreferencesImpl(context, sharedPreferences)
    }

    @Provides
    @AppLife
    internal fun provideDataPreferencesManager(preferencesRepository: DataPreferences): DataPreferencesManager {
        return DataPreferencesManager(preferencesRepository)
    }

    @Provides
    @AppLife
    internal fun provideAppDataPreferencesProvider(context: Context, sharedPreferences: SharedPreferences): AppDataPreferences {
        return AppDataPreferencesImpl(context, sharedPreferences)
    }

    @Provides
    @AppLife
    internal fun provideAppDataPreferencesManager(preferencesRepository: AppDataPreferences): AppDataPreferencesManager {
        return AppDataPreferencesManager(preferencesRepository)
    }
}
