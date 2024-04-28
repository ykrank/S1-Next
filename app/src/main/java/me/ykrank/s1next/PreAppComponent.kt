package me.ykrank.s1next

import android.content.Context
import android.content.SharedPreferences
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.ykrank.androidtools.widget.RxBus
import com.github.ykrank.androidtools.widget.track.DataTrackAgent
import dagger.Component
import me.ykrank.s1next.data.Wifi
import me.ykrank.s1next.data.pref.AppDataPreferencesManager
import me.ykrank.s1next.data.pref.DataPreferencesManager
import me.ykrank.s1next.data.pref.DownloadPreferencesManager
import me.ykrank.s1next.data.pref.GeneralPreferencesManager
import me.ykrank.s1next.data.pref.NetworkPreferencesManager
import me.ykrank.s1next.data.pref.PrefModule
import me.ykrank.s1next.data.pref.ReadPreferencesManager
import me.ykrank.s1next.data.pref.ThemeManager
import okhttp3.CookieJar
import java.net.CookieManager
import javax.inject.Singleton

/**
 * Provides instances of the objects before app init.
 */
@Singleton
@Component(modules = [PreAppModule::class, PrefModule::class])
interface PreAppComponent {
    val context: Context
    val wifi: Wifi
    val jsonMapper: ObjectMapper
    val cookieManager: CookieManager
    val cookieJar: CookieJar
    val rxBus: RxBus
    val dataTrackAgent: DataTrackAgent

    //region SharedPreferences
    val sharedPreferences: SharedPreferences

    val networkPreferencesManager: NetworkPreferencesManager
    val generalPreferencesManager: GeneralPreferencesManager
    val themeManager: ThemeManager
    val downloadPreferencesManager: DownloadPreferencesManager
    val readProgressPreferencesManager: ReadPreferencesManager
    val dataPreferencesManager: DataPreferencesManager

    //endregion
    val appDataPreferencesManager: AppDataPreferencesManager

}
