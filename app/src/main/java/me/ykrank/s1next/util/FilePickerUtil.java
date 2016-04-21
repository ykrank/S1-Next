package me.ykrank.s1next.util;

import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;

import com.nononsenseapps.filepicker.FilePickerActivity;

import java.util.ArrayList;

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
                if (data.getBooleanExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false)) {
                    // For JellyBean and above
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        ClipData clip = data.getClipData();

                        if (clip != null) {
                            for (int i = 0; i < clip.getItemCount(); i++) {
                                Uri uri = clip.getItemAt(i).getUri();
                                // Do something with the URI
                                callback.success(uri);
                            }
                        }
                        // For Ice Cream Sandwich
                    } else {
                        ArrayList<String> paths = data.getStringArrayListExtra
                                (FilePickerActivity.EXTRA_PATHS);

                        if (paths != null) {
                            for (String path : paths) {
                                Uri uri = Uri.parse(path);
                                // Do something with the URI
                                callback.success(uri);
                            }
                        }
                    }

                } else {
                    Uri uri = data.getData();
                    // Do something with the URI
                    callback.success(uri);
                }
            } catch (Exception e) {
                e.printStackTrace();
                callback.error(e);
            }
        }
    }

    public interface OnFilePickCallback {
        void success(@NonNull Uri uri);

        void error(Throwable e);
    }
}
