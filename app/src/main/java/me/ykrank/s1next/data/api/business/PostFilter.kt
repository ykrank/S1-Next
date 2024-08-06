package me.ykrank.s1next.data.api.business

import com.github.ykrank.androidtools.util.FileUtil
import com.github.ykrank.androidtools.util.L
import me.ykrank.s1next.data.api.model.PostAttachment
import me.ykrank.s1next.data.db.biz.BlackListBiz
import me.ykrank.s1next.data.db.dbmodel.BlackList
import org.jsoup.Jsoup
import java.util.Locale
import java.util.regex.Pattern

/**
 * Created by ykrank on 8/6/24
 */
object PostFilter {

    /**
     * After version 4, api not return blockquote, but class div, so replace it
     */
    fun replaceNewQuoteToOld(oReply: String): String {
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
    fun hideBlackListQuote(oReply: String): String {
        var reply = oReply
        val quoteName = findBlockQuoteName(reply)
        if (quoteName != null) {
            reply = replaceQuoteBr(reply)
            reply = replaceTextColor(reply)
            val blackList =
                BlackListBiz.getInstance().getMergedBlackList(-1, quoteName, enableCache = true)
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
        val quote = "<blockquote>[\\s\\S]*</blockquote>".toRegex().find(reply)?.groupValues?.getOrNull(0)
        if (!quote.isNullOrEmpty()) {
            return "<font color=\"#999999\">(.+?) 发表于".toRegex()
                .find(quote)?.groupValues?.getOrNull(1)
        }
        return null
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
        replacedReply = replacedReply.replace("</blockquote>", "</font></blockquote>")
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
    fun replaceBilibiliTag(reply: String): String {
        var reply = reply
        val pattern = Pattern.compile("\\[thgame_biliplay.*?\\[/thgame_biliplay]")
        val matcher = pattern.matcher(reply)
        while (matcher.find()) {
            try {
                val content = matcher.group(0)
                if (content.isNullOrEmpty()){
                    continue
                }
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
                val tagString = String.format(
                    Locale.getDefault(),
                    "<bilibili>http://www.bilibili.com/video/av%d/index_%d.html</bilibili>",
                    avNum,
                    page
                )

                reply = reply.replace(content, tagString)
            } catch (e: Exception) {
                L.leaveMsg(reply)
                L.report("replaceBilibiliTag error", e)
            }

        }
        return reply
    }


    /**
     * 美化帖子格式
     * 去除结尾多余换行
     */
    fun prettifyReply(value: String?): String? {
        if (value.isNullOrEmpty()) {
            return value
        }
        var tReply: String = value
        while (true) {
            if (tReply.endsWith("\r\n")) {
                tReply = tReply.substring(0, tReply.length - 2)
                continue
            }
            if (tReply.endsWith("\n")) {
                tReply = tReply.substring(0, tReply.length - 1)
                continue
            }
            if (tReply.endsWith("<br />")) {
                tReply = tReply.substring(0, tReply.length - 6)
                continue
            }
            break
        }
        return tReply
    }

    /**
     * Replaces attach tags with HTML img tags
     * in order to display attachment images in TextView.
     *
     *
     * Also concats the missing img tag from attachment.
     * See https://github.com/floating-cat/S1-Next/issues/7
     */
    fun processAttachment(
        attachmentMapResult: MutableMap<Int, PostAttachment>,
        reply: String?,
        attachments: Map<Int, PostAttachment>
    ): String? {
        var tReply: String = reply ?: return null

        for ((key, attachment) in attachments) {
            val replyCopy = tReply

            val nTag: String
            if (!attachment.isImage) {
                attachmentMapResult[key] = attachment
                nTag =
                    "<attach href=${attachment.realUrl} name=${attachment.name}>${attachment.name}, ${
                        FileUtil.getPrintSize(
                            attachment.size
                        )
                    }</attach>"
            } else {
                nTag = "\n<img src=\"" + attachment.realUrl + "\" />"
            }
            // get the original string if there is nothing to replace
            tReply = tReply.replace("[attach]$key[/attach]", nTag)

            if (replyCopy == tReply) {
                // concat the missing img tag
                tReply += "<br />"
                tReply += nTag
            }
        }

        return tReply
    }
}