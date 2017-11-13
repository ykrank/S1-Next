package me.ykrank.s1next;

import android.content.Context;
import android.content.SharedPreferences;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.ykrank.androidtools.widget.RxBus;
import com.github.ykrank.androidtools.widget.track.DataTrackAgent;

import java.net.CookieManager;

import javax.inject.Singleton;

import dagger.Component;
import me.ykrank.s1next.data.Wifi;
import me.ykrank.s1next.data.pref.AppDataPreferencesManager;
import me.ykrank.s1next.data.pref.DataPreferencesManager;
import me.ykrank.s1next.data.pref.DownloadPreferencesManager;
import me.ykrank.s1next.data.pref.GeneralPreferencesManager;
import me.ykrank.s1next.data.pref.NetworkPreferencesManager;
import me.ykrank.s1next.data.pref.PrefModule;
import me.ykrank.s1next.data.pref.ReadProgressPreferencesManager;
import me.ykrank.s1next.data.pref.ThemeManager;
import okhttp3.CookieJar;

/**
 * Provides instances of the objects before app init.
 */
@Singleton
@Component(modules = {PreAppModule.class, PrefModule.class})
public interface PreAppComponent {

    Context getContext();

    Wifi getWifi();

    ObjectMapper getJsontMapper();

    CookieManager getCookieManager();

    CookieJar getCookieJar();

    RxBus getRxBus();

    DataTrackAgent getDataTrackAgent();

    //region SharedPreferences
    SharedPreferences getSharedPreferences();

    NetworkPreferencesManager getNetworkPreferencesManager();

    GeneralPreferencesManager getGeneralPreferencesManager();

    ThemeManager getThemeManager();

    DownloadPreferencesManager getDownloadPreferencesManager();

    ReadProgressPreferencesManager getReadProgressPreferencesManager();

    DataPreferencesManager getDataPreferencesManager();

    AppDataPreferencesManager getAppDataPreferencesManager();
    //endregion
}
