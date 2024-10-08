package com.github.ykrank.androidtools.util

import android.content.Context
import com.github.ykrank.androidtools.R

/**
 * Created by ykrank on 2017/11/5.
 */
object DefaultErrorParser : ErrorParser {
    override suspend fun parse(context: Context, throwable: Throwable?): String {
        return throwable?.localizedMessage ?: context.getString(R.string.message_unknown_error)
    }

    override fun ignoreError(throwable: Throwable): Boolean {
        return false
    }
}