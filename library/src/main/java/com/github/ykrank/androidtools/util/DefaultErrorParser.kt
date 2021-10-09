package com.github.ykrank.androidtools.util

import android.content.Context

/**
 * Created by ykrank on 2017/11/5.
 */
object DefaultErrorParser : ErrorParser {
    override fun parse(context: Context, throwable: Throwable): String {
        return throwable.localizedMessage
    }

    override fun ignoreError(throwable: Throwable): Boolean {
        return false
    }
}