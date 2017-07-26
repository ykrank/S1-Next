package me.ykrank.s1next;

import android.content.Context;

import com.google.common.base.Preconditions;

import javax.inject.Singleton;

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
    @Singleton
    OkHttpClient providerDataOkHttpClient(@Data OkHttpClient.Builder builder) {
        Preconditions.checkState("alpha".equals(BuildConfig.BUILD_TYPE));

        return builder.build();
    }

    @Image
    @Provides
    @Singleton
    OkHttpClient providerImageOkHttpClient(@Image OkHttpClient.Builder builder) {
        Preconditions.checkState("alpha".equals(BuildConfig.BUILD_TYPE));

        return builder.build();
    }

    @AppData
    @Provides
    @Singleton
    OkHttpClient providerAppdataOkHttpClient(@AppData OkHttpClient.Builder builder) {
        Preconditions.checkState("alpha".equals(BuildConfig.BUILD_TYPE));

        return builder.build();
    }
}
