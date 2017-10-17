package me.ykrank.s1next.widget

/**
 * Created by ykrank on 2016/6/17.
 */
open class AppException : Exception {

    constructor()

    constructor(msg: String?) : super(msg) {}

    constructor(cause: Throwable) : super(cause) {}

    constructor(message: String?, cause: Throwable) : super(message, cause) {}
}