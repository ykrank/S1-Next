package me.ykrank.s1next.data.api.model

import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

/**
 * Created by ykrank on 7/19/24
 * 
 */
@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true)
data class PostAttachment(
    @JsonProperty("url")
    val urlPrefix: String?,
    @JsonProperty("attachment")
    val urlSuffix: String?,
    @JsonProperty("isimage")
    val isImageStr: String?,
    @JsonProperty("filename")
    val name: String? = null,
    @JsonProperty("filesize")
    val size: Long = 0,
    @JsonProperty("ext")
    val type: String? = null,
) : Parcelable {

    @IgnoredOnParcel
    @get:JsonIgnore
    val realUrl by lazy {
        if (urlPrefix != null && urlSuffix != null) urlPrefix + urlSuffix else "https://img.saraba1st.com/forum/error"
    }

    @IgnoredOnParcel
    @JsonIgnore
    val isImage: Boolean = isImageStr != "0"

}