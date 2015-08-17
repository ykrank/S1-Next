package cl.monsoon.s1next.util;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.support.annotation.StringRes;
import android.widget.Toast;

public final class ClipboardUtil {

    private ClipboardUtil() {}

    /**
     * @param text  The actual text we want to copy.
     * @param resId The resource id of the string resource to show for Toast.
     */
    public static void copyTextAndShowToastPrompt(Activity activity, CharSequence text, @StringRes int resId) {
        ClipboardManager clipboardManager = (ClipboardManager)
                activity.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("simple text", text);
        clipboardManager.setPrimaryClip(clipData);

        ToastUtil.showByResId(resId, Toast.LENGTH_SHORT);
    }
}
