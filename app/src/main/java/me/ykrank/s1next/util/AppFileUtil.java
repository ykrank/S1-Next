package me.ykrank.s1next.util;

import androidx.annotation.NonNull;

import me.ykrank.s1next.BuildConfig;

/**
 * Created by ykrank on 2017/6/6.
 */

public class AppFileUtil {

    /**
     * create a random file name
     */
    @NonNull
    public static String createRandomFileName(@NonNull String suffix) {
        String name = BuildConfig.APPLICATION_ID.replace(".", "_") + System.currentTimeMillis();
        return name + suffix;
    }
}
