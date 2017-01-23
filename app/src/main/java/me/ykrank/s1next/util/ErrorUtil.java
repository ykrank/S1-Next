package me.ykrank.s1next.util;

import android.content.Context;
import android.support.annotation.NonNull;

import com.bugsnag.android.Severity;

import java.io.IOException;

import me.ykrank.s1next.BuildConfig;
import me.ykrank.s1next.R;
import me.ykrank.s1next.data.api.ApiException;
import retrofit2.adapter.rxjava2.HttpException;

public final class ErrorUtil {

    private static final String TAG_LOG = ErrorUtil.class.getSimpleName();

    public static String parse(@NonNull Context context, Throwable throwable) {
        if (throwable instanceof ApiException) {
            return throwable.getLocalizedMessage();
        } else if (throwable instanceof IOException) {
            return context.getString(R.string.message_network_error);
        } else if (throwable instanceof HttpException) {
            return context.getString(R.string.message_server_error);
        } else {
            L.e(TAG_LOG, throwable);
            return context.getString(R.string.message_unknown_error);
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
