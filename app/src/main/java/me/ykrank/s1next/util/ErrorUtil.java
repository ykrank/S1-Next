package me.ykrank.s1next.util;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;

import me.ykrank.s1next.BuildConfig;
import me.ykrank.s1next.R;
import me.ykrank.s1next.data.api.ApiException;
import retrofit2.HttpException;

public final class ErrorUtil {

    private static final String TAG_LOG = ErrorUtil.class.getSimpleName();

    @NonNull
    public static String parse(@NonNull Context context, final Throwable throwable) {
        String msg = parseNetError(context, throwable);
        Throwable cause = throwable.getCause();
        while (msg == null && cause != null) {
            msg = parseNetError(context, cause);
            cause = cause.getCause();
        }
        if (msg == null) {
            L.e(TAG_LOG, throwable);
            return context.getString(R.string.message_unknown_error);
        }
        return msg;
    }

    @Nullable
    private static String parseNetError(@NonNull Context context, Throwable throwable) {
        if (throwable instanceof ApiException) {
            return throwable.getLocalizedMessage();
        } else if (throwable instanceof IOException) {
            return context.getString(R.string.message_network_error);
        } else if (throwable instanceof HttpException) {
            String msg = throwable.getLocalizedMessage();
            if (TextUtils.isEmpty(msg)) {
                return context.getString(R.string.message_server_error);
            }
            return msg;
        }
        return null;
    }

    public static void throwNewErrorIfDebug(RuntimeException throwable) {
        if (BuildConfig.DEBUG) {
            throw throwable;
        } else {
            L.report(throwable, Log.WARN);
        }
    }
}
