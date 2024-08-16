package me.ykrank.s1next.data.api.model

import android.graphics.Color
import androidx.annotation.IntDef
import androidx.annotation.WorkerThread
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.JsonNode
import com.github.ykrank.androidtools.ui.adapter.StableIdModel
import com.github.ykrank.androidtools.ui.adapter.model.DiffSameItem
import com.github.ykrank.androidtools.util.L
import com.github.ykrank.androidtools.util.MathUtil
import me.ykrank.s1next.App
import me.ykrank.s1next.data.api.Api
import me.ykrank.s1next.data.api.business.PostFilter
import me.ykrank.s1next.util.HtmlUtils
import me.ykrank.s1next.util.JsonUtil
import paperparcel.PaperParcel
import paperparcel.PaperParcelable
import java.util.Locale
import java.util.regex.Pattern

@PaperParcel
@JsonIgnoreProperties(ignoreUnknown = true)
class Post : PaperParcelable, Cloneable, DiffSameItem, StableIdModel {

    @JsonProperty("pid")
    var id: Int = 0
    @JsonProperty("author")
    var authorName: String? = null
    @JsonProperty("authorid")
    var authorId: String? = null

    @JsonProperty("_reply")
    var reply: String? = null

    @JsonProperty("_isFirst")
    var isFirst: Boolean = false
    @JsonProperty("number")
    var number: String? = null
    @JsonProperty("dbdateline")
    var dateTime: Long = 0
    @JsonProperty("groupid")
    var groupId: Int = 0

    /**
     * 只保留非图片附件，图片附件自动插入到帖子中
     */
    @JsonProperty("_attachment")
    var attachmentMap = mutableMapOf<Int, PostAttachment>()
    /**
     * is in blacklist
     */
    @JsonProperty("_hide")
    @HideFLag
    var hide: Int = HIDE_NO

    @JsonProperty("_remark")
    var remark: String? = null

    @JsonProperty("_isTrade")
    var isTrade: Boolean = false

    @JsonProperty("_isVote")
    var isVote: Boolean = false

    @JsonProperty("_extraHtml")
    var extraHtml: String? = null

    @JsonProperty("_banned")
    var banned: Boolean = false
    /**
     * Null if no rates, empty if not init.
     */
    @JsonProperty("_rates")
    var rates: List<Rate>? = null
    /**
     * whether the author of this post is Original Poster
     */
    @JsonProperty("_isOp")
    var isOpPost: Boolean = false

    constructor()

    @JsonCreator
    constructor(@JsonProperty("first") first: String?, @JsonProperty("message") reply: String?,
                @JsonProperty("attachments") attachments: JsonNode?) {
        //if "attachments" is empty, it's array, but map if not empty
        this.isFirst = "1" == first
        var tReply = filterReply(reply)
        if (attachments != null && attachments.isObject) {
            val attachmentMap = App.preAppComponent.jsonMapper.let {
                JsonUtil.readJsonNode(
                    it,
                    attachments,
                    object : TypeReference<Map<Int, PostAttachment>>() {})
            }
            tReply = PostFilter.processAttachment(this.attachmentMap, tReply, attachmentMap)
        }
        tReply = PostFilter.prettifyReply(tReply)
        this.reply = tReply
    }

    override val stableId: Long
        get() = id.toLong()

    public override fun clone(): Post {
        var o: Post? = null
        try {
            o = super.clone() as Post
        } catch (e: CloneNotSupportedException) {
            L.e(TAG, e)
        } catch (e: ClassCastException) {
            L.e(TAG, e)
        }

        return o ?: Post()
    }

    override fun isSameItem(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false

        other as Post

        if (id != other.id) return false
        if (authorName != other.authorName) return false
        if (authorId != other.authorId) return false

        return true
    }

    override fun isSameContent(other: Any?): Boolean {
        return equals(other)
    }

    fun getPage(): Int {
        return MathUtil.divide(number?.toInt() ?: 1, Api.POSTS_PER_PAGE)
    }

    @WorkerThread
    private fun filterReply(value: String?): String? {
        if (value.isNullOrEmpty()) {
            return value
        }

        if ("提示: <em>作者被禁止或删除 内容自动屏蔽</em>" == value) {
            this.banned = true
        }

        // 替换手动unicode，比如 &#x4E0D;
        var tReply: String = HtmlUtils.unescapeHtml(value) ?: value

        tReply = PostFilter.replaceCodeDivId(tReply)
        tReply = PostFilter.replaceNewQuoteToOld(tReply)
        tReply = PostFilter.hideBlackListQuote(tReply)
        tReply = PostFilter.replaceBilibiliTag(tReply)

        // Replaces "imgwidth" with "img width",
        // because some img tags in S1 aren't correct.
        // This may be the best way to deal with it though
        // we may replace something wrong by accident.
        // Also maps some colors, see mapColors(String).
        tReply = mapColors(tReply).replace("<imgwidth=\"".toRegex(), "<img width=\"")

        return tReply
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Post

        if (id != other.id) return false
        if (authorName != other.authorName) return false
        if (authorId != other.authorId) return false
        if (reply != other.reply) return false
        if (isFirst != other.isFirst) return false
        if (number != other.number) return false
        if (dateTime != other.dateTime) return false
        if (groupId != other.groupId) return false
        if (attachmentMap != other.attachmentMap) return false
        if (hide != other.hide) return false
        if (remark != other.remark) return false
        if (isTrade != other.isTrade) return false
        if (isVote != other.isVote) return false
        if (extraHtml != other.extraHtml) return false
        if (banned != other.banned) return false
        if (rates != other.rates) return false
        if (isOpPost != other.isOpPost) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + (authorName?.hashCode() ?: 0)
        result = 31 * result + (authorId?.hashCode() ?: 0)
        result = 31 * result + (reply?.hashCode() ?: 0)
        result = 31 * result + isFirst.hashCode()
        result = 31 * result + (number?.hashCode() ?: 0)
        result = 31 * result + dateTime.hashCode()
        result = 31 * result + groupId
        result = 31 * result + attachmentMap.hashCode()
        result = 31 * result + hide
        result = 31 * result + (remark?.hashCode() ?: 0)
        result = 31 * result + isTrade.hashCode()
        result = 31 * result + isVote.hashCode()
        result = 31 * result + (extraHtml?.hashCode() ?: 0)
        result = 31 * result + banned.hashCode()
        result = 31 * result + (rates?.hashCode() ?: 0)
        result = 31 * result + isOpPost.hashCode()
        return result
    }

    @IntDef(HIDE_NO, HIDE_USER, HIDE_WORD)
    annotation class HideFLag

    companion object {
        const val HIDE_NO = 0
        const val HIDE_USER = 1
        const val HIDE_WORD = 2

        private val TAG = Post::class.java.simpleName
        private val COLOR_NAME_MAP: androidx.collection.SimpleArrayMap<String, String> = androidx.collection.SimpleArrayMap()

        init {

            COLOR_NAME_MAP.put("sienna", "#A0522D")
            COLOR_NAME_MAP.put("darkolivegreen", "#556B2F")
            COLOR_NAME_MAP.put("darkgreen", "#006400")
            COLOR_NAME_MAP.put("darkslateblue", "#483D8B")
            COLOR_NAME_MAP.put("indigo", "#4B0082")
            COLOR_NAME_MAP.put("darkslategray", "#2F4F4F")
            COLOR_NAME_MAP.put("darkred", "#8B0000")
            COLOR_NAME_MAP.put("darkorange", "#FF8C00")
            COLOR_NAME_MAP.put("slategray", "#708090")
            COLOR_NAME_MAP.put("dimgray", "#696969")
            COLOR_NAME_MAP.put("sandybrown", "#F4A460")
            COLOR_NAME_MAP.put("yellowgreen", "#9ACD32")
            COLOR_NAME_MAP.put("seagreen", "#2E8B57")
            COLOR_NAME_MAP.put("mediumturquoise", "#48D1CC")
            COLOR_NAME_MAP.put("royalblue", "#4169E1")
            COLOR_NAME_MAP.put("orange", "#FFA500")
            COLOR_NAME_MAP.put("deepskyblue", "#00BFFF")
            COLOR_NAME_MAP.put("darkorchid", "#9932CC")
            COLOR_NAME_MAP.put("pink", "#FFC0CB")
            COLOR_NAME_MAP.put("wheat", "#F5DEB3")
            COLOR_NAME_MAP.put("lemonchiffon", "#FFFACD")
            COLOR_NAME_MAP.put("palegreen", "#98FB98")
            COLOR_NAME_MAP.put("paleturquoise", "#AFEEEE")
            COLOR_NAME_MAP.put("lightblue", "#ADD8E6")

            // https://code.google.com/p/android/issues/detail?id=75953
            COLOR_NAME_MAP.put("white", "#FFFFFF")
        }

        @JvmField
        val CREATOR = PaperParcelPost.CREATOR

        /**
         * [Color] doesn't support all HTML color names.
         * So [android.text.Html.fromHtml] won't
         * map some color names for replies in S1.
         * We need to map these color names to their hex value.
         */
        private fun mapColors(reply: String): String {
            // example: color="sienna"
            // matcher.group(0): color="sienna"
            // matcher.group(1): sienna
            val matcher = Pattern.compile("color=\"([a-zA-Z]+)\"").matcher(reply)

            val stringBuffer = StringBuffer()
            var color: String?
            while (matcher.find()) {
                // get color hex value for its color name
                val name = matcher.group(1) ?: continue
                color = COLOR_NAME_MAP.get(name.lowercase(Locale.US))
                if (color == null) {
                    continue
                }
                // append part of the string and its color hex value
                matcher.appendReplacement(stringBuffer, "color=\"$color\"")
            }
            matcher.appendTail(stringBuffer)

            return stringBuffer.toString()
        }
    }
}
