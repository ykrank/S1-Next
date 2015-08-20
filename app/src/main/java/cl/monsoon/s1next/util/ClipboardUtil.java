package cl.monsoon.s1next.util;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.support.annotation.StringRes;

public final class ClipboardUtil {

    private ClipboardUtil() {}

    /**
     * @param text  The actual text we want to copy.
     * @param resId The resource id of the string resource to show for Toast.
     */
    public static void copyTextAndShowToastPrompt(Context context, CharSequence text, @StringRes int resId) {
        ClipboardManager clipboardManager = (ClipboardManager)
                context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("simple text", text);
        clipboardManager.setPrimaryClip(clipData);

        ToastUtil.showShortToastByResId(context, resId);
    }
}
