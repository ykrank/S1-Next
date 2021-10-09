package com.github.ykrank.androidtools.widget.uploadimg

import com.github.ykrank.androidtools.widget.AppException

class ImageUploadError : AppException {

    constructor()

    constructor(msg: String?) : super(msg) {}

    constructor(cause: Throwable?) : super(cause) {}

    constructor(message: String?, cause: Throwable) : super(message, cause) {}
}