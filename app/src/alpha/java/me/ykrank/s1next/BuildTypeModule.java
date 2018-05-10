package me.ykrank.s1next;

import android.content.Context;

import com.github.ykrank.androidtools.guava.Preconditions;

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
        Preconditions.checkState("alpha".equals(BuildConfig.BUILD_TYPE));

        return builder.build();
    }

    @Image
    @Provides
    @AppLife
    OkHttpClient providerImageOkHttpClient(@Image OkHttpClient.Builder builder) {
        Preconditions.checkState("alpha".equals(BuildConfig.BUILD_TYPE));

        return builder.build();
    }

    @AppData
    @Provides
    @AppLife
    OkHttpClient providerAppdataOkHttpClient(@AppData OkHttpClient.Builder builder) {
        Preconditions.checkState("alpha".equals(BuildConfig.BUILD_TYPE));

        return builder.build();
    }
}
