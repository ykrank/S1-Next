package me.ykrank.s1next.data.api.model

import android.graphics.Color
import androidx.annotation.IntDef
import androidx.annotation.WorkerThread
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnore
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
import me.ykrank.s1next.data.db.biz.BlackListBiz
import me.ykrank.s1next.data.db.dbmodel.BlackList
import me.ykrank.s1next.util.JsonUtil
import org.jsoup.Jsoup
import paperparcel.PaperParcel
import paperparcel.PaperParcelable
import java.util.*
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
    @JsonIgnore
    var reply: String? = null
    @JsonIgnore
    var isFirst: Boolean = false
    @JsonProperty("number")
    var number: String? = null
    @JsonProperty("dbdateline")
    var dateTime: Long = 0
    @JsonProperty("groupid")
    var groupId: Int = 0
    @JsonIgnore
    var attachmentMap: Map<Int, Attachment> = mapOf()
    /**
     * is in blacklist
     */
    @JsonIgnore
    @HideFLag
    var hide: Int = Hide_No
    @JsonIgnore
    var remark: String? = null
    @JsonIgnore
    var isTrade: Boolean = false
    @JsonIgnore
    var isVote: Boolean = false
    @JsonIgnore
    var extraHtml: String? = null
    @JsonIgnore
    var banned: Boolean = false
    /**
     * Null if no rates, empty if not init.
     */
    @JsonIgnore
    var rates: List<Rate>? = null
    /**
     * whether the author of this post is Original Poster
     */
    @JsonIgnore
    var isOpPost: Boolean = false

    constructor()

    @JsonCreator
    constructor(@JsonProperty("first") first: String?, @JsonProperty("message") reply: String?,
                @JsonProperty("attachments") attachments: JsonNode?) {
        //if "attachments" is empty, it's array, but map if not empty
        this.isFirst = "1" == first
        this.reply = filterReply(reply)
        if (attachments != null && attachments.isObject) {
            this.attachmentMap = App.preAppComponent.jsonMapper.let {
                JsonUtil.readJsonNode(it, attachments, object : TypeReference<Map<Int, Attachment>>() {})
            }
            this.reply = processAttachment()
        }
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

        var tReply: String = value!!
        tReply = replaceNewQuoteToOld(tReply)
        tReply = hideBlackListQuote(tReply)

        tReply = replaceBilibiliTag(tReply)

        // Replaces "imgwidth" with "img width",
        // because some img tags in S1 aren't correct.
        // This may be the best way to deal with it though
        // we may replace something wrong by accident.
        // Also maps some colors, see mapColors(String).
        tReply = mapColors(tReply).replace("<imgwidth=\"".toRegex(), "<img width=\"")

        return tReply
    }

    /**
     * After version 4, api not return blockquote, but class div, so replace it
     */
    private fun replaceNewQuoteToOld(oReply: String): String {
        if (oReply.contains("<div class=\"reply_wrap\">")) {
            try {
                val document = Jsoup.parse(oReply)
                val oReplyElements = document.select("div.reply_wrap")

                oReplyElements.forEach {
                    it.clearAttributes()
                    it.tagName("blockquote")
                }
                //get the closest parent element
                return oReplyElements.parents().first()!!.html()
            } catch (e: Exception) {
                L.report(e)
            }
        }

        return oReply
    }

    /**
     * 隐藏黑名单用户的引用内容

     * @param reply
     * *
     * @return
     */
    private fun hideBlackListQuote(oReply: String): String {
        var reply = oReply
        val quoteName = findBlockQuoteName(reply)
        if (quoteName != null) {
            reply = replaceQuoteBr(reply)
            reply = replaceTextColor(reply)
            val blackList = BlackListBiz.getInstance().getMergedBlackList(-1, quoteName)
            if (blackList != null && blackList.post != BlackList.NORMAL) {
                return replaceBlockQuoteContent(reply, blackList.remark)
            }
        }
        return reply
    }

    /**
     * 解析引用对象的用户名

     * @param reply
     * *
     * @return
     */
    private fun findBlockQuoteName(reply: String): String? {
        var name: String? = null
        var pattern = Pattern.compile("<blockquote>[\\s\\S]*</blockquote>")
        var matcher = pattern.matcher(reply)
        if (matcher.find()) {
            val quote = matcher.group(0)
            pattern = Pattern.compile("<font color=\"#999999\">(.+?) 发表于")
            matcher = pattern.matcher(quote)
            if (matcher.find()) {
                name = matcher.group(1)
            }
        }
        return name
    }

    /**
     * 替换引用时多余的&lt;br/&gt;标记

     * @param reply
     * *
     * @return
     */
    private fun replaceQuoteBr(reply: String): String {
        return reply.replace("</blockquote></div><br />", "</blockquote></div>")
    }

    /**
     * 替换引用时字体的颜色
     *
     * @param reply
     *
     * @return
     *
     */

    private fun replaceTextColor(reply: String): String {
        var replacedReply = reply.replace("<blockquote>", "<font color=\"#999999\"><blockquote>")
        replacedReply = replacedReply.replace("</blockquote>","</font></blockquote>")
        return replacedReply
    }


    /**
     * 替换对已屏蔽对象的引用内容

     * @param reply
     * *
     * @param remark
     * *
     * @return
     */
    private fun replaceBlockQuoteContent(reply: String, remark: String?): String {
        var pattern = Pattern.compile("</font></a>[\\s\\S]*</blockquote>")
        var matcher = pattern.matcher(reply)
        val reText: String
        if (matcher.find()) {
            reText = "</font></a><br />\r\n[已被抹布]</blockquote>"
            return reply.replaceFirst("</font></a>[\\s\\S]*</blockquote>".toRegex(), reText)
        } else {
            pattern = Pattern.compile("</font><br />[\\s\\S]*</blockquote>")
            matcher = pattern.matcher(reply)
            if (matcher.find()) {
                reText = "</font><br />\r\n[已被抹布]</blockquote>"
                return reply.replaceFirst("</font><br />[\\s\\S]*</blockquote>".toRegex(), reText)
            }
        }
        return reply
    }

    /**
     * 将B站链接添加自定义Tag
     * like "<bilibili>http://www.bilibili.com/video/av6706141/index_3.html</bilibili>"

     * @param reply
     * *
     * @return
     */
    private fun replaceBilibiliTag(reply: String): String {
        var reply = reply
        val pattern = Pattern.compile("\\[thgame_biliplay.*?\\[/thgame_biliplay\\]")
        val matcher = pattern.matcher(reply)
        while (matcher.find()) {
            try {
                val content = matcher.group(0)
                //find av number
                val avPattern = Pattern.compile("\\{,=av\\}[0-9]+")
                val avMatcher = avPattern.matcher(content)
                if (!avMatcher.find()) {
                    continue
                }
                val avNum = Integer.valueOf(avMatcher.group().substring(6))
                //find page
                var page = 1
                val pagePattern = Pattern.compile("\\{,=page\\}[0-9]+")
                val pageMatcher = pagePattern.matcher(content)
                if (pageMatcher.find()) {
                    page = Integer.valueOf(pageMatcher.group().substring(8))
                }

                //like "<bilibili>http://www.bilibili.com/video/av6706141/index_3.html</bilibili>"
                val tagString = String.format(Locale.getDefault(),
                        "<bilibili>http://www.bilibili.com/video/av%d/index_%d.html</bilibili>", avNum, page)

                reply = reply.replace(content, tagString)
            } catch (e: Exception) {
                L.leaveMsg(reply)
                L.report("replaceBilibiliTag error", e)
            }

        }
        return reply
    }

    /**
     * Replaces attach tags with HTML img tags
     * in order to display attachment images in TextView.
     *
     *
     * Also concats the missing img tag from attachment.
     * See https://github.com/floating-cat/S1-Next/issues/7
     */
    private fun processAttachment(): String? {
        var tReply: String = reply ?: return null

        for ((key, attachment) in attachmentMap) {
            val imgTag = "\n<img src=\"" + attachment.url + "\" />"
            val replyCopy = tReply
            // get the original string if there is nothing to replace
            tReply = tReply.replace("[attach]$key[/attach]", imgTag)

            if (replyCopy == tReply) {
                // concat the missing img tag
                tReply += imgTag
            }
        }

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

    @PaperParcel
    @JsonIgnoreProperties(ignoreUnknown = true)
    class Attachment : PaperParcelable {
        @JsonIgnore
        var url: String? = null

        constructor()

        @JsonCreator
        constructor(@JsonProperty("url") urlPrefix: String?,
                    @JsonProperty("attachment") urlSuffix: String?) {
            url = if (urlPrefix != null && urlSuffix != null) urlPrefix + urlSuffix else "http://img.saraba1st.com/forum/error"
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other?.javaClass != javaClass) return false

            other as Attachment

            if (url != other.url) return false

            return true
        }

        override fun hashCode(): Int {
            return url?.hashCode() ?: 0
        }

        companion object {
            @JvmField
            val CREATOR = PaperParcelPost_Attachment.CREATOR
        }
    }

    @IntDef(Hide_No, Hide_User, Hide_Word)
    annotation class HideFLag

    companion object {
        const val Hide_No = 0
        const val Hide_User = 1
        const val Hide_Word = 2

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
                color = COLOR_NAME_MAP.get(matcher.group(1).toLowerCase(Locale.US))
                if (color == null) {
                    continue
                }
                // append part of the string and its color hex value
                matcher.appendReplacement(stringBuffer, "color=\"" + color + "\"")
            }
            matcher.appendTail(stringBuffer)

            return stringBuffer.toString()
        }
    }
}
