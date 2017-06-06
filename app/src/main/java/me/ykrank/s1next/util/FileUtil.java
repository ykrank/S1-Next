package me.ykrank.s1next.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;

import java.io.File;
import java.io.IOException;

import me.ykrank.s1next.BuildConfig;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;

/**
 * Created by ykrank on 2017/6/6.
 */

public class FileUtil {

    /**
     * get system download directory
     */
    @NonNull
    public static File getDownloadDirectory(@NonNull Context context) {
        String throwable = null;
        File file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

        if (file != null) {
            if (file.exists()) {
                if (file.isDirectory()) {
                    return file;
                }
            } else if (file.mkdirs()) {
                return file;
            }
        }

        file = context.getFilesDir();
        if (file == null) {
            throwable = "Failed to get local storage directory";
        } else if (file.exists()) {
            if (!file.isDirectory()) {
                throwable = file.getAbsolutePath() + " already exists and is not a directory";
            }
        } else {
            if (!file.mkdirs()) {
                throwable = "Unable to create directory: " + file.getAbsolutePath();
            }
        }

        if (throwable != null) {
            throw new IllegalStateException(throwable);
        }

        return file;
    }

    /**
     * create a new file in directory
     */
    @NonNull
    public static File newFileInDirectory(@NonNull File parent, @NonNull String suffix) {
        String name = BuildConfig.APPLICATION_ID.replace(".", "_") + System.currentTimeMillis();
        File file = new File(parent, name + suffix);
        for (int i = 0; file.exists(); i++) {
            file = new File(parent, name + "_" + i + suffix);
        }
        return file;
    }

    public static void copyFile(@NonNull File source, @NonNull File sink) throws IOException {
        BufferedSource bufferedSource = null;
        BufferedSink bufferedSink = null;
        try {
            bufferedSource = Okio.buffer(Okio.source(source));
            bufferedSink = Okio.buffer(Okio.sink(sink));
            bufferedSink.writeAll(bufferedSource);
        } finally {
            if (bufferedSink != null) {
                try {
                    bufferedSink.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (bufferedSource != null) {
                try {
                    bufferedSource.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void notifyImageInMediaStore(@NonNull Context context, @NonNull File file) {
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + file.getAbsolutePath())));
    }
}
