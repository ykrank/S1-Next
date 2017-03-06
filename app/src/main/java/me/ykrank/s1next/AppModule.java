package me.ykrank.s1next;

import android.content.Context;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import me.ykrank.s1next.data.User;
import me.ykrank.s1next.data.Wifi;
import me.ykrank.s1next.data.api.Api;
import me.ykrank.s1next.data.api.S1Service;
import me.ykrank.s1next.data.api.UserValidator;
import me.ykrank.s1next.data.db.AppDaoSessionManager;
import me.ykrank.s1next.viewmodel.UserViewModel;
import me.ykrank.s1next.widget.AppDaoOpenHelper;
import me.ykrank.s1next.widget.EventBus;
import me.ykrank.s1next.widget.PersistentHttpCookieStore;
import me.ykrank.s1next.widget.glide.OkHttpNoAvatarInterceptor;
import me.ykrank.s1next.widget.track.DataTrackAgent;
import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Provides instances of the objects when we need to inject.
 */
@Module
public final class AppModule {

    private final App mApp;

    public AppModule(App app) {
        this.mApp = app;
    }

    @Provides
    @Singleton
    Context provideContext() {
        return mApp;
    }

    @Provides
    @Singleton
    CookieManager providerCookieManager(Context context) {
        return new CookieManager(new PersistentHttpCookieStore(context), CookiePolicy.ACCEPT_ALL);
    }

    @Provides
    @Singleton
    OkHttpClient providerOkHttpClient(CookieManager cookieManager) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(17, TimeUnit.SECONDS);
        builder.writeTimeout(17, TimeUnit.SECONDS);
        builder.readTimeout(77, TimeUnit.SECONDS);
        builder.retryOnConnectionFailure(true);
        builder.cookieJar(new JavaNetCookieJar(cookieManager));
        builder.addNetworkInterceptor(new OkHttpNoAvatarInterceptor());
        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
            httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.HEADERS);
            builder.addInterceptor(httpLoggingInterceptor);
        }

        return builder.build();
    }

    @Provides
    @Singleton
    S1Service providerRetrofit(OkHttpClient okHttpClient) {
        return new Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(Api.BASE_API_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(JacksonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
                .create(S1Service.class);
    }

    @Provides
    @Singleton
    EventBus providerEventBus() {
        return new EventBus();
    }

    @Provides
    @Singleton
    User providerUser(UserViewModel userViewModel) {
        return userViewModel.getUser();
    }

    @Provides
    @Singleton
    UserValidator providerUserValidator(User user) {
        return new UserValidator(user);
    }

    @Provides
    @Singleton
    UserViewModel providerUserViewModel() {
        return new UserViewModel();
    }

    @Provides
    @Singleton
    Wifi providerWifi() {
        return new Wifi();
    }

    @Provides
    @Singleton
    ObjectMapper provideJsonObjectMapper() {
        return new ObjectMapper();
    }

    @Provides
    @Singleton
    DataTrackAgent provideDataTrackAgent() {
        return new DataTrackAgent();
    }

    @Provides
    @Singleton
    AppDaoOpenHelper provideAppDaoOpenHelper(Context context) {
        return new AppDaoOpenHelper(context, BuildConfig.DB_NAME);
    }

    @Provides
    @Singleton
    AppDaoSessionManager provideAppDaoSessionManager(AppDaoOpenHelper helper) {
        return new AppDaoSessionManager(helper);
    }
}
