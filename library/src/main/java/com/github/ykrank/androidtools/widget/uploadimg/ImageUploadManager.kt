package com.github.ykrank.androidtools.widget.uploadimg

import io.reactivex.Single
import java.io.File
import java.io.FileDescriptor

interface ImageUploadManager {
    fun uploadImage(imageFile: File): Single<ImageUpload>

    fun uploadImage(imageFile: FileDescriptor): Single<ImageUpload>

    fun delUploadedImage(url: String): Single<ImageDelete>
}

