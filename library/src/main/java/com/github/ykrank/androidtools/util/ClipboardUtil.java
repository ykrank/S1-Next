package com.github.ykrank.androidtools.util;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;

public final class ClipboardUtil {

    private ClipboardUtil() {
    }

    /**
     * Copies text.
     *
     * @param text The actual text we want to copyFrom.
     */
    public static void copyText(Context context, CharSequence label, CharSequence text) {
        ClipboardManager clipboardManager = (ClipboardManager)
                context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText(label, text);
        if (clipboardManager != null) {
            clipboardManager.setPrimaryClip(clipData);
        }
    }
}
