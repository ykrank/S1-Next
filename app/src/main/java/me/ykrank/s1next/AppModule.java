package me.ykrank.s1next;

import android.content.Context;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.ykrank.androidtools.widget.EditorDiskCache;
import com.github.ykrank.androidtools.widget.NullTrustManager;
import com.github.ykrank.androidtools.widget.RxBus;

import java.io.File;
import java.security.SecureRandom;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import dagger.Module;
import dagger.Provides;
import io.rx_cache2.internal.RxCache;
import io.victoralbertos.jolyglot.JacksonSpeaker;
import me.ykrank.s1next.data.User;
import me.ykrank.s1next.data.api.Api;
import me.ykrank.s1next.data.api.ApiCacheProvider;
import me.ykrank.s1next.data.api.ApiVersionInterceptor;
import me.ykrank.s1next.data.api.S1Service;
import me.ykrank.s1next.data.api.UserValidator;
import me.ykrank.s1next.data.api.app.AppApi;
import me.ykrank.s1next.data.api.app.AppService;
import me.ykrank.s1next.data.api.app.AppTokenInterceptor;
import me.ykrank.s1next.data.pref.AppDataPreferencesManager;
import me.ykrank.s1next.data.pref.DownloadPreferencesManager;
import me.ykrank.s1next.data.pref.NetworkPreferencesManager;
import me.ykrank.s1next.task.AutoSignTask;
import me.ykrank.s1next.viewmodel.UserViewModel;
import me.ykrank.s1next.widget.RawJsonConverterFactory;
import me.ykrank.s1next.widget.download.ImageDownloadManager;
import me.ykrank.s1next.widget.glide.AvatarUrlsCache;
import me.ykrank.s1next.widget.glide.OkHttpNoAvatarInterceptor;
import me.ykrank.s1next.widget.hostcheck.AppHostUrl;
import me.ykrank.s1next.widget.hostcheck.AppMultiHostInterceptor;
import me.ykrank.s1next.widget.hostcheck.NoticeCheckTask;
import me.ykrank.s1next.widget.net.AppData;
import me.ykrank.s1next.widget.net.AppDns;
import me.ykrank.s1next.widget.net.Data;
import me.ykrank.s1next.widget.net.Image;
import okhttp3.CookieJar;
import okhttp3.Dns;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Provides instances of the objects when we need to inject.
 */
@Module(includes = BuildTypeModule.class)
public final class AppModule {

    @Provides
    @AppLife
    AppHostUrl provideBaseHostUrl(NetworkPreferencesManager networkPreferencesManager) {
        return new AppHostUrl(networkPreferencesManager);
    }

    @Provides
    @AppLife
    Dns provideHttpDns(Context context, AppHostUrl baseHostUrl) {
        return new AppDns(context, baseHostUrl);
    }

    @Data
    @Provides
    @AppLife
    OkHttpClient.Builder providerDataOkHttpClientBuilder(CookieJar cookieJar, AppHostUrl baseHostUrl, Dns dns) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.dns(dns);
        builder.connectTimeout(10, TimeUnit.SECONDS);
        builder.writeTimeout(20, TimeUnit.SECONDS);
        builder.readTimeout(10, TimeUnit.SECONDS);
        builder.retryOnConnectionFailure(true);
        builder.cookieJar(cookieJar);
        builder.addInterceptor(new ApiVersionInterceptor());
        builder.addInterceptor(new AppMultiHostInterceptor(baseHostUrl));

        return builder;
    }

    @AppData
    @Provides
    @AppLife
    OkHttpClient.Builder providerAppDataOkHttpClientBuilder(CookieJar cookieJar, User user) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(10, TimeUnit.SECONDS);
        builder.writeTimeout(20, TimeUnit.SECONDS);
        builder.readTimeout(10, TimeUnit.SECONDS);
        builder.retryOnConnectionFailure(true);
        builder.cookieJar(cookieJar);
        builder.addNetworkInterceptor(new AppTokenInterceptor(user));

        return builder;
    }

    @Image
    @Provides
    @AppLife
    OkHttpClient.Builder providerImageOkHttpClientBuilder(CookieJar cookieJar, AppHostUrl baseHostUrl, Dns dns) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.dns(dns);
        builder.connectTimeout(17, TimeUnit.SECONDS);
        builder.writeTimeout(17, TimeUnit.SECONDS);
        builder.readTimeout(77, TimeUnit.SECONDS);
        builder.retryOnConnectionFailure(true);
        builder.cookieJar(cookieJar);
        builder.addNetworkInterceptor(new OkHttpNoAvatarInterceptor());
        builder.addInterceptor(new AppMultiHostInterceptor(baseHostUrl));

        //trust https
        try {
            X509TrustManager trustManager = new NullTrustManager();
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[]{trustManager}, new SecureRandom());
            builder.sslSocketFactory(sslContext.getSocketFactory(), trustManager);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return builder;
    }

    @Provides
    @AppLife
    S1Service providerRetrofit(@Data OkHttpClient okHttpClient, ObjectMapper mapper) {
        return new Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(Api.BASE_API_URL)
                .addConverterFactory(RawJsonConverterFactory.Companion.create())
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(JacksonConverterFactory.create(mapper))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
                .create(S1Service.class);
    }

    @Provides
    @AppLife
    AppService providerAppRetrofit(@AppData OkHttpClient okHttpClient) {
        return new Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(AppApi.BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(JacksonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
                .create(AppService.class);
    }

    @Provides
    @AppLife
    ApiCacheProvider providerApiCacheProvider(Context context, DownloadPreferencesManager downloadPreferencesManager) {
        String cachePath = context.getCacheDir().getAbsolutePath() + "/rx_cache";
        File cacheDir = new File(cachePath);
        if (!cacheDir.exists()) {
            cacheDir.mkdirs();
        }
        return new RxCache.Builder()
                .useExpiredDataIfLoaderNotAvailable(true)
                .setMaxMBPersistenceCache(downloadPreferencesManager.getTotalDataCacheSize())
                .persistence(new File(cachePath), new JacksonSpeaker())
                .using(ApiCacheProvider.class);
    }

    @Provides
    @AppLife
    User providerUser(UserViewModel userViewModel) {
        return userViewModel.getUser();
    }

    @Provides
    @AppLife
    AutoSignTask provideAutoSignTask(S1Service s1Service, User user) {
        return new AutoSignTask(s1Service, user);
    }

    @Provides
    @AppLife
    UserValidator providerUserValidator(User user, AutoSignTask autoSignTask) {
        return new UserValidator(user, autoSignTask);
    }

    @Provides
    @AppLife
    UserViewModel providerUserViewModel(AppDataPreferencesManager appDataPreferencesManager) {
        return new UserViewModel(appDataPreferencesManager);
    }

    @Provides
    @AppLife
    NoticeCheckTask provideNoticeCheckTask(RxBus rxBus, S1Service s1Service, User user) {
        return new NoticeCheckTask(rxBus, s1Service, user);
    }

    @Provides
    @AppLife
    EditorDiskCache provideEditorDiskCache(Context context) {
        return new EditorDiskCache(context.getCacheDir().getPath()
                + File.separator + "editor_disk_cache");
    }

    @Provides
    @AppLife
    AvatarUrlsCache provideAvatarUrlsCache() {
        return new AvatarUrlsCache();
    }

    @Provides
    @AppLife
    ImageDownloadManager provideImageDownloadManager(@Image OkHttpClient.Builder okHttpClientBuilder) {
        return new ImageDownloadManager(okHttpClientBuilder);
    }
}
