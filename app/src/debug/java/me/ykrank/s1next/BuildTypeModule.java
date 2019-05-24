package me.ykrank.s1next;

import android.content.Context;

import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.google.common.base.Preconditions;
import com.github.ykrank.androidtools.widget.NullTrustManager;

import java.security.SecureRandom;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import dagger.Module;
import dagger.Provides;
import me.ykrank.s1next.widget.net.AppData;
import me.ykrank.s1next.widget.net.Data;
import me.ykrank.s1next.widget.net.Image;
import okhttp3.OkHttpClient;

/**
 * Provides instances of the objects according to build type when we need to inject.
 */
@Module
public final class BuildTypeModule {

    public BuildTypeModule(Context context) {
    }

    @Data
    @Provides
    @AppLife
    OkHttpClient providerDataOkHttpClient(@Data OkHttpClient.Builder builder) {
        Preconditions.checkState("debug".equals(BuildConfig.BUILD_TYPE));

        //trust https
        try {
            X509TrustManager trustManager = new NullTrustManager();
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[]{trustManager}, new SecureRandom());
            builder.sslSocketFactory(sslContext.getSocketFactory(), trustManager);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //Stetho
        builder.addNetworkInterceptor(new StethoInterceptor());

        return builder.build();
    }

    @Image
    @Provides
    @AppLife
    OkHttpClient providerImageOkHttpClient(@Image OkHttpClient.Builder builder) {
        Preconditions.checkState("debug".equals(BuildConfig.BUILD_TYPE));

        //trust https
        try {
            X509TrustManager trustManager = new NullTrustManager();
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[]{trustManager}, new SecureRandom());
            builder.sslSocketFactory(sslContext.getSocketFactory(), trustManager);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //Stetho
        builder.addNetworkInterceptor(new StethoInterceptor());

        return builder.build();
    }

    @AppData
    @Provides
    @AppLife
    OkHttpClient providerAppdataOkHttpClient(@AppData OkHttpClient.Builder builder) {
        Preconditions.checkState("debug".equals(BuildConfig.BUILD_TYPE));

        //trust https
        try {
            X509TrustManager trustManager = new NullTrustManager();
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[]{trustManager}, new SecureRandom());
            builder.sslSocketFactory(sslContext.getSocketFactory(), trustManager);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //Stetho
        builder.addNetworkInterceptor(new StethoInterceptor());

        return builder.build();
    }
}
