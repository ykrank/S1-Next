package me.ykrank.s1next.util;

import android.support.annotation.StringRes;

import java.io.IOException;

import me.ykrank.s1next.R;
import retrofit2.adapter.rxjava.HttpException;

public final class ErrorUtil {

    private static final String TAG_LOG = ErrorUtil.class.getSimpleName();

    @StringRes
    public static int parse(Throwable throwable) {
        if (throwable instanceof IOException) {
            return R.string.message_network_error;
        } else if (throwable instanceof HttpException) {
            return R.string.message_server_error;
        } else {
            L.e(TAG_LOG, throwable);
            return R.string.message_unknown_error;
        }
    }
}
