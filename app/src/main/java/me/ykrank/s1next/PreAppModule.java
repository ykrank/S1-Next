package me.ykrank.s1next;

import android.content.Context;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.ykrank.androidtools.widget.PersistentHttpCookieStore;
import com.github.ykrank.androidtools.widget.RxBus;
import com.github.ykrank.androidtools.widget.track.DataTrackAgent;

import java.net.CookieManager;
import java.net.CookiePolicy;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import me.ykrank.s1next.data.Wifi;
import okhttp3.CookieJar;
import okhttp3.JavaNetCookieJar;

/**
 * Provides instances of the objects before app init.
 */
@Module
public final class PreAppModule {

    private final App mApp;

    public PreAppModule(App app) {
        this.mApp = app;
    }

    @Provides
    @Singleton
    Context provideContext() {
        return mApp;
    }

    @Provides
    @Singleton
    Wifi providerWifi() {
        return new Wifi();
    }

    @Provides
    @Singleton
    ObjectMapper provideJsonObjectMapper() {
        return new ObjectMapper()
                .configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true)
                .configure(JsonParser.Feature.IGNORE_UNDEFINED, true);
    }

    @Provides
    @Singleton
    CookieManager providerCookieManager(Context context) {
        return new CookieManager(new PersistentHttpCookieStore(context), CookiePolicy.ACCEPT_ALL);
    }

    @Provides
    @Singleton
    CookieJar providerCookieJar(CookieManager cookieManager) {
        return new JavaNetCookieJar(cookieManager);
    }

    @Provides
    @Singleton
    RxBus providerRxBus() {
        return new RxBus();
    }

    @Provides
    @Singleton
    DataTrackAgent provideDataTrackAgent() {
        return new DataTrackAgent();
    }
}
