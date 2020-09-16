package me.ykrank.s1next.widget.uploadimg.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.github.ykrank.androidtools.widget.uploadimg.ImageUpload

@JsonIgnoreProperties(ignoreUnknown = true)
class RIPImageUpload {


    /**
     * code : success
     * */
    var code: String? = null

    /**
     *
    "size": 197939,
    "path": "/0/ab7e2f063127169fcb172a26956db293/IMG_CMP_142950607.jpeg",
    "url": "https://p.sda1.dev/0/ab7e2f063127169fcb172a26956db293/IMG_CMP_142950607.jpeg",
    "delete_url": "https://p.sda1.dev/api/v1/delete/0/ab7e2f063127169fcb172a26956db293/e6a05e316650b21e"
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
        model.deleteUrl = data?.deleteUrl
        return model
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    class DataBean {
        var size: Int = 0
        var path: String? = null
        var url: String? = null

        @JsonProperty("delete_url")
        var deleteUrl: String? = null

        override fun toString(): String {
            return "DataBean(size=$size, path=$path, url=$url, deleteUrl=$deleteUrl)"
        }
    }
}