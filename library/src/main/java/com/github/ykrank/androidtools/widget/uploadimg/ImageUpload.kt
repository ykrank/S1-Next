package com.github.ykrank.androidtools.widget.uploadimg

class ImageUpload {
    var success: Boolean = false
    var msg: String? = null
    var url: String? = null
    var deleteUrl: String? = null

    override fun toString(): String {
        return "ImageUpload(success=$success, msg=$msg, url=$url, deleteUrl=$deleteUrl)"
    }
}