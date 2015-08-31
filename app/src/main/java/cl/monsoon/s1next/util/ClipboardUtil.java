package cl.monsoon.s1next.util;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;

public final class ClipboardUtil {

    private ClipboardUtil() {}

    /**
     * Copies text.
     *
     * @param text The actual text we want to copy.
     */
    public static void copyText(Context context, CharSequence text) {
        ClipboardManager clipboardManager = (ClipboardManager)
                context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("simple text", text);
        clipboardManager.setPrimaryClip(clipData);
    }
}
