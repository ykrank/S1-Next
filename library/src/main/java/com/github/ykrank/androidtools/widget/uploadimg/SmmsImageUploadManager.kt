package com.github.ykrank.androidtools.widget.uploadimg

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.github.ykrank.androidtools.util.L
import io.reactivex.Single
import okhttp3.ExperimentalOkHttpApi
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.jsoup.Jsoup
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.jackson.JacksonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Url
import java.io.File
import java.io.FileDescriptor
import java.util.concurrent.TimeUnit

class SmmsImageUploadManager(_okHttpClient: OkHttpClient? = null) : ImageUploadManager {

    private val okHttpClient: OkHttpClient by lazy {
        _okHttpClient ?: OkHttpClient.Builder()
                .connectTimeout(17, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(120, TimeUnit.SECONDS)
                .build()
    }

    private val uploadApiService: SmmsImageUploadApiService by lazy {
        Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl("https://sm.ms/")
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(JacksonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
                .create(SmmsImageUploadApiService::class.java)
    }

    /**
     * Force upload to sm.ms
     */
    override fun uploadImage(imageFile: File): Single<ImageUpload> {
        val requestFile = imageFile.asRequestBody("image/*".toMediaTypeOrNull())
        val body = MultipartBody.Part.createFormData("smfile", imageFile.name, requestFile)
        return uploadApiService.postSmmsImage(body).map { it.toCommon() }
    }

    @OptIn(ExperimentalOkHttpApi::class)
    override fun uploadImage(imageFile: FileDescriptor): Single<ImageUpload> {
        val requestFile = imageFile.toRequestBody("image/*".toMediaTypeOrNull())
        val body = MultipartBody.Part.createFormData("smfile", "image.jpg", requestFile)
        return uploadApiService.postSmmsImage(body).map { it.toCommon() }
    }

    override fun delUploadedImage(url: String): Single<ImageDelete> {
        return uploadApiService.deldSmmsImage(url)
                .map { SmmsImageDelete.fromHtml(it).toCommon() }
    }
}

interface SmmsImageUploadApiService {
    @Headers("Accept:*/*", "Accept-Language:zh-CN,zh;q=0.8", "User-Agent:Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2454.101 Safari/537.36")
    @Multipart
    @POST("https://sm.ms/api/upload")
    fun postSmmsImage(@Part image: MultipartBody.Part?): Single<SmmsImageUpload>

    @Headers("Accept:*/*", "Accept-Language:zh-CN,zh;q=0.8", "User-Agent:Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2454.101 Safari/537.36")
    @GET
    fun deldSmmsImage(@Url url: String?): Single<String>
}


@JsonIgnoreProperties(ignoreUnknown = true)
class SmmsImageDelete {

    /**
     * msg: "Clear file success"
     */
    var msg: String? = null

    val success: Boolean get() = msg == "File delete success." || msg == "File already deleted."

    override fun toString(): String {
        return "ImageDelete(msg=$msg)"
    }

    fun toCommon(): ImageDelete {
        val model = ImageDelete()
        model.msg = msg
        model.success = success
        return model
    }

    companion object {
        fun fromHtml(html: String): SmmsImageDelete {
            val model = SmmsImageDelete()
            try {
                val document = Jsoup.parse(html)
                model.msg = document.selectFirst("div.container")?.text()?.trim()
            } catch (e: Exception) {
                L.report(e)
            }

            return model
        }
    }
}

@JsonIgnoreProperties(ignoreUnknown = true)
class SmmsImageUpload {


    /**
     * code : success
     * data : {"width":1157,"height":680,"filename":"image_2015-08-26_10-54-48.png","storename":"56249afa4e48b.png","size":69525,"path":"/2015/10/19/56249afa4e48b.png","hash":"nLbCw63NheaiJp1","timestamp":1445239546,"url":"https://ooo.0o0.ooo/2015/10/19/56249afa4e48b.png","delete":"https://sm.ms/api/delete/nLbCw63NheaiJp1"}
     */

    var code: String? = null
    /**
     * width : 1157
     * height : 680
     * filename : image_2015-08-26_10-54-48.png
     * storename : 56249afa4e48b.png
     * size : 69525
     * path : /2015/10/19/56249afa4e48b.png
     * hash : nLbCw63NheaiJp1
     * timestamp : 1445239546
     * url : https://ooo.0o0.ooo/2015/10/19/56249afa4e48b.png
     * delete : https://sm.ms/api/delete/nLbCw63NheaiJp1
     */

    var data: DataBean? = null

    val success: Boolean get() = code == "success"

    override fun toString(): String {
        return "ImageUpload(code=$code, data=$data)"
    }

    fun toCommon(): ImageUpload {
        val model = ImageUpload()
        model.success = success
        model.msg = code
        model.url = data?.url
        model.deleteUrl = data?.delete
        return model
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    class DataBean {
        var width: Int = 0
        var height: Int = 0
        var filename: String? = null
        var storename: String? = null
        var size: Int = 0
        var path: String? = null
        var hash: String? = null
        var timestamp: Int = 0
        var url: String? = null
        var delete: String? = null

        override fun toString(): String {
            return "DataBean(width=$width, height=$height, filename=$filename, storename=$storename, size=$size, path=$path, hash=$hash, timestamp=$timestamp, url=$url, delete=$delete)"
        }
    }
}