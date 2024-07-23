package me.ykrank.s1next

import android.content.Context
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.ykrank.androidtools.widget.EditorDiskCache
import com.github.ykrank.androidtools.widget.EventBus
import dagger.Module
import dagger.Provides
import me.ykrank.s1next.data.User
import me.ykrank.s1next.data.api.Api
import me.ykrank.s1next.data.api.ApiCacheProvider
import me.ykrank.s1next.data.api.ApiVersionInterceptor
import me.ykrank.s1next.data.api.S1Service
import me.ykrank.s1next.data.api.UserValidator
import me.ykrank.s1next.data.api.app.AppApi
import me.ykrank.s1next.data.api.app.AppService
import me.ykrank.s1next.data.api.app.AppTokenInterceptor
import me.ykrank.s1next.data.cache.api.S1ApiCacheProvider
import me.ykrank.s1next.data.cache.biz.CacheBiz
import me.ykrank.s1next.data.cache.biz.CacheGroupBiz
import me.ykrank.s1next.data.pref.AppDataPreferencesManager
import me.ykrank.s1next.data.pref.DownloadPreferencesManager
import me.ykrank.s1next.data.pref.NetworkPreferencesManager
import me.ykrank.s1next.task.AutoSignTask
import me.ykrank.s1next.viewmodel.UserViewModel
import me.ykrank.s1next.widget.RawJsonConverterFactory
import me.ykrank.s1next.widget.download.DownloadProgressInterceptor
import me.ykrank.s1next.widget.download.ProgressManager
import me.ykrank.s1next.widget.glide.AvatarUrlsCache
import me.ykrank.s1next.widget.glide.OkHttpNoAvatarInterceptor
import me.ykrank.s1next.widget.hostcheck.AppHostUrl
import me.ykrank.s1next.widget.hostcheck.AppMultiHostInterceptor
import me.ykrank.s1next.widget.hostcheck.NoticeCheckTask
import me.ykrank.s1next.widget.net.AppData
import me.ykrank.s1next.widget.net.AppDns
import me.ykrank.s1next.widget.net.Data
import me.ykrank.s1next.widget.net.Image
import okhttp3.CookieJar
import okhttp3.Dns
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.jackson.JacksonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit

/**
 * Provides instances of the objects when we need to inject.
 */
@Module
class AppModule {
    @Provides
    @AppLife
    fun provideBaseHostUrl(networkPreferencesManager: NetworkPreferencesManager): AppHostUrl {
        return AppHostUrl(networkPreferencesManager)
    }

    @Provides
    @AppLife
    fun provideHttpDns(context: Context, baseHostUrl: AppHostUrl): Dns {
        return AppDns(context, baseHostUrl)
    }

    @Data
    @Provides
    @AppLife
    fun providerDataOkHttpClientBuilder(
        cookieJar: CookieJar,
        baseHostUrl: AppHostUrl,
        dns: Dns
    ): OkHttpClient.Builder {
        val builder = OkHttpClient.Builder()
        builder.dns(dns)
        builder.connectTimeout(10, TimeUnit.SECONDS)
        builder.writeTimeout(20, TimeUnit.SECONDS)
        builder.readTimeout(10, TimeUnit.SECONDS)
        builder.retryOnConnectionFailure(true)
        builder.cookieJar(cookieJar)
        builder.addInterceptor(ApiVersionInterceptor())
        builder.addInterceptor(AppMultiHostInterceptor(baseHostUrl))
        return builder
    }

    @AppData
    @Provides
    @AppLife
    fun providerAppDataOkHttpClientBuilder(
        cookieJar: CookieJar,
        user: User
    ): OkHttpClient.Builder {
        val builder = OkHttpClient.Builder()
        builder.connectTimeout(10, TimeUnit.SECONDS)
        builder.writeTimeout(20, TimeUnit.SECONDS)
        builder.readTimeout(10, TimeUnit.SECONDS)
        builder.retryOnConnectionFailure(true)
        builder.cookieJar(cookieJar)
        builder.addNetworkInterceptor(AppTokenInterceptor(user))
        return builder
    }

    @Image
    @Provides
    @AppLife
    fun providerImageOkHttpClientBuilder(
        cookieJar: CookieJar,
        baseHostUrl: AppHostUrl,
        dns: Dns,
        progressManager: ProgressManager,
    ): OkHttpClient.Builder {
        val builder = OkHttpClient.Builder()
        builder.dns(dns)
        builder.connectTimeout(17, TimeUnit.SECONDS)
        builder.writeTimeout(17, TimeUnit.SECONDS)
        builder.readTimeout(77, TimeUnit.SECONDS)
        builder.retryOnConnectionFailure(true)
        builder.cookieJar(cookieJar)
        builder.addNetworkInterceptor(OkHttpNoAvatarInterceptor())
        builder.addNetworkInterceptor(DownloadProgressInterceptor(progressManager))
        builder.addInterceptor(AppMultiHostInterceptor(baseHostUrl))
        return builder
    }

    @Provides
    @AppLife
    fun providerRetrofit(@Data okHttpClient: OkHttpClient, mapper: ObjectMapper): S1Service {
        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(Api.BASE_API_URL)
            .addConverterFactory(RawJsonConverterFactory.create())
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(JacksonConverterFactory.create(mapper))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
            .create(S1Service::class.java)
    }

    @Provides
    @AppLife
    fun providerAppRetrofit(@AppData okHttpClient: OkHttpClient): AppService {
        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(AppApi.BASE_URL)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(JacksonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
            .create(AppService::class.java)
    }

    @Provides
    @AppLife
    fun providerApiCacheProvider(
        context: Context,
        downloadPreferencesManager: DownloadPreferencesManager,
        s1Service: S1Service,
        cacheBiz: CacheBiz,
        cacheGroupBiz: CacheGroupBiz,
        user: User,
        jsonMapper: ObjectMapper,
    ): ApiCacheProvider {
        return S1ApiCacheProvider(
            downloadPreferencesManager,
            s1Service,
            cacheBiz,
            cacheGroupBiz,
            user,
            jsonMapper
        )
    }

    @Provides
    @AppLife
    fun providerUser(userViewModel: UserViewModel): User {
        return userViewModel.user
    }

    @Provides
    @AppLife
    fun provideAutoSignTask(s1Service: S1Service, user: User): AutoSignTask {
        return AutoSignTask(s1Service, user)
    }

    @Provides
    @AppLife
    fun providerUserValidator(user: User, autoSignTask: AutoSignTask): UserValidator {
        return UserValidator(user, autoSignTask)
    }

    @Provides
    @AppLife
    fun providerUserViewModel(appDataPreferencesManager: AppDataPreferencesManager): UserViewModel {
        return UserViewModel(appDataPreferencesManager)
    }

    @Provides
    @AppLife
    fun provideNoticeCheckTask(eventBus: EventBus, s1Service: S1Service, user: User): NoticeCheckTask {
        return NoticeCheckTask(eventBus, s1Service, user)
    }

    @Provides
    @AppLife
    fun provideEditorDiskCache(context: Context): EditorDiskCache {
        return EditorDiskCache(
            context.cacheDir.path
                    + File.separator + "editor_disk_cache"
        )
    }

    @Provides
    @AppLife
    fun provideAvatarUrlsCache(): AvatarUrlsCache {
        return AvatarUrlsCache()
    }

    @Provides
    @AppLife
    fun provideProgressManager(): ProgressManager {
        return ProgressManager()
    }
}
