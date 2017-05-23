package me.ykrank.s1next.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;

import com.nononsenseapps.filepicker.FilePickerActivity;
import com.nononsenseapps.filepicker.Utils;

import java.io.File;
import java.util.List;

/**
 * Created by AdminYkrank on 2016/4/20.
 * 路径选择器
 */
public class FilePickerUtil {

    public static Intent dirPickIntent(Context context) {
        Intent i = new Intent(context, FilePickerActivity.class);
        i.putExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false);
        i.putExtra(FilePickerActivity.EXTRA_ALLOW_CREATE_DIR, true);
        i.putExtra(FilePickerActivity.EXTRA_MODE, FilePickerActivity.MODE_DIR);
        i.putExtra(FilePickerActivity.EXTRA_START_PATH, Environment.getExternalStorageDirectory().getPath());

        return i;
    }

    public static Intent filePickIntent(Context context) {
        Intent i = new Intent(context, FilePickerActivity.class);
        i.putExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false);
        i.putExtra(FilePickerActivity.EXTRA_ALLOW_CREATE_DIR, false);
        i.putExtra(FilePickerActivity.EXTRA_MODE, FilePickerActivity.MODE_FILE);
        i.putExtra(FilePickerActivity.EXTRA_START_PATH, Environment.getExternalStorageDirectory().getPath());
        return i;
    }

    public static void onFilePickResult(int resultCode, @NonNull Intent data, OnFilePickCallback callback) {
        if (resultCode == Activity.RESULT_OK) {
            try {
                // Use the provided utility method to parse the result
                List<Uri> files = Utils.getSelectedFilesFromResult(data);
                for (Uri uri : files) {
                    File file = Utils.getFileForUri(uri);
                    // Do something with the result...
                    callback.success(file);
                }
            } catch (Exception e) {
                L.report(e);
                callback.error(e);
            }
        } else {
            callback.cancel();
        }
    }

    public interface OnFilePickCallback {
        void success(@NonNull File file);

        void cancel();

        void error(Throwable e);
    }
}
