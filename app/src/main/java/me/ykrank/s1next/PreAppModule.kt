package me.ykrank.s1next

import android.content.Context
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.ykrank.androidtools.widget.EventBus
import com.github.ykrank.androidtools.widget.PersistentHttpCookieStore
import com.github.ykrank.androidtools.widget.track.DataTrackAgent
import dagger.Module
import dagger.Provides
import me.ykrank.s1next.data.Wifi
import okhttp3.CookieJar
import okhttp3.JavaNetCookieJar
import java.net.CookieManager
import java.net.CookiePolicy
import javax.inject.Singleton

/**
 * Provides instances of the objects before app init.
 */
@Module
class PreAppModule(private val mApp: App) {
    @Provides
    @Singleton
    fun provideContext(): Context {
        return mApp
    }

    @Provides
    @Singleton
    fun providerWifi(): Wifi {
        return Wifi()
    }

    @Provides
    @Singleton
    fun provideJsonObjectMapper(): ObjectMapper {
        return ObjectMapper()
            .configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true)
            .configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true)
            .configure(JsonParser.Feature.IGNORE_UNDEFINED, true)
            .configure(JsonParser.Feature.INCLUDE_SOURCE_IN_LOCATION, BuildConfig.DEBUG)
    }

    @Provides
    @Singleton
    fun providerCookieManager(context: Context): CookieManager {
        return CookieManager(PersistentHttpCookieStore(context), CookiePolicy.ACCEPT_ALL)
    }

    @Provides
    @Singleton
    fun providerCookieJar(cookieManager: CookieManager): CookieJar {
        return JavaNetCookieJar(cookieManager)
    }

    @Provides
    @Singleton
    fun providerRxBus(): EventBus {
        return EventBus()
    }

    @Provides
    @Singleton
    fun provideDataTrackAgent(): DataTrackAgent {
        return DataTrackAgent()
    }
}
