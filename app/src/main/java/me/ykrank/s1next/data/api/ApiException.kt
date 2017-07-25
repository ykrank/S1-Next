package me.ykrank.s1next.data.api

/**
 * Created by ykrank on 2016/6/17.
 */
open class ApiException : Exception {

    constructor(msg: String?) : super(msg) {}

    constructor(cause: Throwable) : super(cause) {}

    constructor(message: String?, cause: Throwable) : super(message, cause) {}

    class AuthenticityTokenException : ApiException {

        constructor(msg: String?) : super(msg) {}

        constructor(cause: Throwable) : super(cause) {}

        constructor(message: String?, cause: Throwable) : super(message, cause) {}
    }

    class ApiServerException : ApiException {

        constructor(msg: String?) : super(msg) {}

        constructor(cause: Throwable) : super(cause) {}

        constructor(message: String?, cause: Throwable) : super(message, cause) {}
    }

    class AppServerException(msg: String?, val code: Int) : ApiException(msg)
}
