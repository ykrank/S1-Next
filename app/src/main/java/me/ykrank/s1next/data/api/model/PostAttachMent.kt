package me.ykrank.s1next.data.api.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import paperparcel.PaperParcel
import paperparcel.PaperParcelable

/**
 * Created by yuanke on 7/19/24
 * @author yuanke.ykrank@bytedance.com
 */
@PaperParcel
@JsonIgnoreProperties(ignoreUnknown = true)
class PostAttachment : PaperParcelable {

    @JsonProperty("_img")
    val imageUrl: String

    constructor(imageUrl: String) {
        this.imageUrl = imageUrl
    }

    @JsonCreator
    constructor(
        @JsonProperty("url") urlPrefix: String? = null,
        @JsonProperty("attachment") urlSuffix: String?
    ) {
        imageUrl =
            if (urlPrefix != null && urlSuffix != null) urlPrefix + urlSuffix else "https://img.saraba1st.com/forum/error"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false

        other as PostAttachment

        if (imageUrl != other.imageUrl) return false

        return true
    }

    override fun hashCode(): Int {
        return imageUrl.hashCode()
    }

    companion object {
        @JvmField
        val CREATOR = PaperParcelPostAttachment.CREATOR
    }
}