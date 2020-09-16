package me.ykrank.s1next.widget.uploadimg

import com.github.ykrank.androidtools.widget.uploadimg.ImageDelete
import com.github.ykrank.androidtools.widget.uploadimg.ImageUpload
import com.github.ykrank.androidtools.widget.uploadimg.ImageUploadManager
import io.reactivex.Single
import me.ykrank.s1next.widget.uploadimg.model.RIPImageUpload
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.jackson.JacksonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import java.io.File
import java.util.concurrent.TimeUnit

//赞美坛友R.I.P提供的图库服务
class RIPImageUploadManager(_okHttpClient: OkHttpClient? = null) : ImageUploadManager {

    private val okHttpClient: OkHttpClient by lazy {
        _okHttpClient ?: OkHttpClient.Builder()
                .connectTimeout(17, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(120, TimeUnit.SECONDS)
                .build()
    }

    private val uploadApiService: RIPImageUploadApiService by lazy {
        Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl("https://p.sda1.dev/api/v1/")
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(JacksonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
                .create(RIPImageUploadApiService::class.java)
    }

    /**
     * Force upload to sm.ms
     */
    override fun uploadImage(imageFile: File): Single<ImageUpload> {
        val requestFile = imageFile.asRequestBody("image/*".toMediaTypeOrNull())
        return uploadApiService.postRIPImage(imageFile.name
                , requestFile).map { it.toCommon() }
    }

    override fun delUploadedImage(url: String): Single<ImageDelete> {
        return Single.just(ImageDelete().apply { success = true })
    }
}

interface RIPImageUploadApiService {
    @POST("https://p.sda1.dev/api/v1/upload_external_noform")
    fun postRIPImage(@Query("filename") fileName: String, @Body image: RequestBody?): Single<RIPImageUpload>
}

