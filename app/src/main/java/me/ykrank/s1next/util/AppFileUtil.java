package me.ykrank.s1next.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.ImageHeaderParser;
import com.bumptech.glide.load.ImageHeaderParserUtils;
import com.bumptech.glide.load.engine.bitmap_recycle.ArrayPool;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import me.ykrank.s1next.BuildConfig;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;

/**
 * Created by ykrank on 2017/6/6.
 */

public class AppFileUtil {

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
     * create a random file name
     */
    @NonNull
    public static String createRandomFileName(@NonNull String suffix) {
        String name = BuildConfig.APPLICATION_ID.replace(".", "_") + System.currentTimeMillis();
        return name + suffix;
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

    @NonNull
    public static ImageHeaderParser.ImageType getImageType(@NonNull Context context, @NonNull File file) throws IOException {
        List<ImageHeaderParser> parsers = Glide.get(context).getRegistry().getImageHeaderParsers();
        ArrayPool arrayPool = Glide.get(context).getArrayPool();
        if (parsers != null && arrayPool != null) {
            return ImageHeaderParserUtils.getType(parsers, new FileInputStream(file), arrayPool);
        }
        return ImageHeaderParser.ImageType.UNKNOWN;
    }

    @Nullable
    public static String getImageTypeSuffix(ImageHeaderParser.ImageType imageType) {
        switch (imageType) {
            case JPEG:
                return ".jpg";
            case GIF:
                return ".gif";
            case PNG:
            case PNG_A:
                return ".png";
            case RAW:
                return ".raw";
            case WEBP:
            case WEBP_A:
                return ".webp";
            default:
                return null;
        }
    }
}
