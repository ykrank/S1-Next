package com.github.ykrank.androidtools.widget.uploadimg

import io.reactivex.Single
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.jackson.JacksonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit

interface ImageUploadManager {
    fun uploadImage(imageFile: File): Single<ImageUpload>

    fun delUploadedImage(url: String): Single<ImageDelete>
}

