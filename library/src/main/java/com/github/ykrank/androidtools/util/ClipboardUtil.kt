package com.github.ykrank.androidtools.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context

object ClipboardUtil {
    /**
     * Copies text.
     *
     * @param text The actual text we want to copyFrom.
     */
    fun copyText(context: Context, label: CharSequence?, text: CharSequence?) {
        val clipboardManager =
            context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
        val clipData = ClipData.newPlainText(label, text)
        clipboardManager?.setPrimaryClip(clipData)
    }
}
