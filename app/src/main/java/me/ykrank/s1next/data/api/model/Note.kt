package me.ykrank.s1next.data.api.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonSetter
import com.github.ykrank.androidtools.ui.adapter.StableIdModel
import com.github.ykrank.androidtools.ui.adapter.model.DiffSameItem
import paperparcel.PaperParcel
import paperparcel.PaperParcelable
import java.util.regex.Pattern

/**
 * Created by ykrank on 2017/1/5.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@PaperParcel
class Note : PaperParcelable, DiffSameItem, StableIdModel {
    @JsonProperty("author")
    var author: String? = null
    @JsonProperty("authorid")
    var authorId: String? = null
    @JsonProperty("dateline")
    var dateline: Long = 0
    @JsonProperty("id")
    var id: String? = null
    @JsonIgnore
    private var isNew: Boolean = false
    @JsonIgnore
    var note: String? = null
    //eg forum.php?mod=redirect&goto=findpost&ptid=1220112&pid=1
    @JsonIgnore
    var url: String? = null
    @JsonIgnore
    var content: String? = null

    @JsonCreator
    constructor(@JsonProperty("note") note: String) {
        this.note = note
        //eg <a href="home.php?mod=space&uid=1">someone</a> 回复了您的帖子 <a href="forum.php?mod=redirect&goto=findpost&ptid=1220112&pid=1" target="_blank">【Android】 s1Next-鹅版-v0.7.2（群522433035）</a> &nbsp; <a href="forum.php?mod=redirect&goto=findpost&pid=34692327&ptid=1220112" target="_blank" class="lit">查看</a>
        var pattern = Pattern.compile("<a href=\"(forum\\.php\\?mod=redirect&goto=findpost.+?)\"")
        var matcher = pattern.matcher(note)
        if (matcher.find()) {
            url = matcher.group(1)
        }
        pattern = Pattern.compile("target=\"_blank\">(.+)</a> &nbsp;")
        matcher = pattern.matcher(note)
        if (matcher.find()) {
            content = matcher.group(1)
        }
    }

    fun isNew(): Boolean {
        return isNew
    }

    fun setNew(aNew: Boolean) {
        isNew = aNew
    }

    @JsonSetter("new")
    fun setNew(aNew: Int) {
        isNew = aNew > 0
    }

    override fun isSameItem(other: Any): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Note

        if (id != other.id) return false
        return true
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Note

        if (author != other.author) return false
        if (authorId != other.authorId) return false
        if (dateline != other.dateline) return false
        if (id != other.id) return false
        if (isNew != other.isNew) return false
        if (note != other.note) return false
        if (url != other.url) return false
        if (content != other.content) return false

        return true
    }

    override fun hashCode(): Int {
        var result = author?.hashCode() ?: 0
        result = 31 * result + (authorId?.hashCode() ?: 0)
        result = 31 * result + dateline.hashCode()
        result = 31 * result + (id?.hashCode() ?: 0)
        result = 31 * result + isNew.hashCode()
        result = 31 * result + (note?.hashCode() ?: 0)
        result = 31 * result + (url?.hashCode() ?: 0)
        result = 31 * result + (content?.hashCode() ?: 0)
        return result
    }

    companion object {

        @JvmField
        val CREATOR = PaperParcelNote.CREATOR
    }

}
