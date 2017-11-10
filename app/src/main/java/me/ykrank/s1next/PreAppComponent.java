package me.ykrank.s1next;

import android.content.Context;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.ykrank.androidtools.widget.RxBus;
import com.github.ykrank.androidtools.widget.track.DataTrackAgent;

import java.net.CookieManager;

import javax.inject.Singleton;

import dagger.Component;
import me.ykrank.s1next.data.Wifi;
import okhttp3.CookieJar;

/**
 * Provides instances of the objects before app init.
 */
@Singleton
@Component(modules = {PreAppModule.class})
public interface PreAppComponent {

    Context getContext();

    Wifi getWifi();

    ObjectMapper getJsontMapper();

    CookieManager getCookieManager();

    CookieJar getCookieJar();

    RxBus getRxBus();

    DataTrackAgent getDataTrackAgent();
}
