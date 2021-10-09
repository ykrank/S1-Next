package com.github.ykrank.androidtools.util

import android.content.Context

/**
 * Created by ykrank on 2017/11/5.
 */
interface ErrorParser {

    /**
     * 从错误中解析用户友好的提示
     */
    fun parse(context: Context, throwable: Throwable): String

    /**
     * 是否忽略异常不上传
     */
    fun ignoreError(throwable: Throwable):Boolean
}

class IgnoredException:Exception{
    constructor():super()
    constructor(message: String?) : super(message)
    constructor(message: String?, cause: Throwable?) : super(message, cause)
    constructor(cause: Throwable?) : super(cause)
}