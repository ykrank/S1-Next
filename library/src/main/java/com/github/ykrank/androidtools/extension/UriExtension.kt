package com.github.ykrank.androidtools.extension

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import androidx.core.net.toFile
import java.io.FileInputStream
import java.io.InputStream

/**
 * Created by ykrank on 11/8/24
 */

@SuppressLint("Recycle")
fun Uri.stream(context: Context): InputStream? {
    return if (ContentResolver.SCHEME_CONTENT == scheme) {
        context.contentResolver.openInputStream(this)
    } else {
        FileInputStream(this.toFile())
    }
}