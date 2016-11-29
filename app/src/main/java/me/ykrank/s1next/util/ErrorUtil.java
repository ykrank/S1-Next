package me.ykrank.s1next.util;

import android.support.annotation.StringRes;

import com.bugsnag.android.Severity;

import java.io.IOException;

import me.ykrank.s1next.BuildConfig;
import me.ykrank.s1next.R;
import me.ykrank.s1next.data.api.ApiException;
import retrofit2.adapter.rxjava.HttpException;

public final class ErrorUtil {

    private static final String TAG_LOG = ErrorUtil.class.getSimpleName();

    @StringRes
    public static int parse(Throwable throwable) {
        if (throwable instanceof ApiException) {
            return R.string.message_api_error;
        } else if (throwable instanceof IOException) {
            return R.string.message_network_error;
        } else if (throwable instanceof HttpException) {
            return R.string.message_server_error;
        } else {
            L.e(TAG_LOG, throwable);
            return R.string.message_unknown_error;
        }
    }

    public static void throwNewError(RuntimeException throwable) {
        if (BuildConfig.DEBUG) {
            throw throwable;
        } else {
            L.report(throwable, Severity.WARNING);
        }
    }
}
