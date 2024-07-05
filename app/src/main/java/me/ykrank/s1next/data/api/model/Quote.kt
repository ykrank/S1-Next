package me.ykrank.s1next.data.api.model

import me.ykrank.s1next.util.HtmlUtils

data class Quote(

    /**
     * The quoted user identification which was encoded in server.
     * Without this we can't notify the user.
     */
    var encodedUserId: String,
    /**
     * The processed quoted content which has some HTML tags and
     * its origin redirect hyperlink.
     */
    var quoteMessage: String
) {

    companion object {
        /**
         * Extracts [Quote] from XML string.
         */
        fun fromXmlString(xmlString: String?): Quote? {
            if (xmlString.isNullOrEmpty()) return null
            // example: <input type="hidden" name="noticeauthor" value="d755gUR1jP9eeoTPkiOyz3FxvLzpFLJsSFvJA8uAfBg" />
            val encodedUserId = "name=\"noticeauthor\"\\svalue=\"(\\p{ASCII}+)\"\\s/>".toRegex()
                .find(xmlString)?.groupValues?.getOrNull(1)
            if (encodedUserId != null) {
                // example: <input type="hidden" name="noticetrimstr" value="[post][size=2][url=forum.php?mod=redirect&amp;goto=findpost&amp;pid=1&amp;ptid=1][color=#999999]VVV 发表于 2014-12-13 10:11[/color][/url][/size]
                val quoteMessageStr =
                    "name=\"noticetrimstr\"\\svalue=\"(.+?)\"\\s/>".toRegex(RegexOption.DOT_MATCHES_ALL)
                        .find(xmlString)?.groupValues?.getOrNull(1)
                // unescape ampersand (&amp;)
                val quoteMessage = HtmlUtils.unescapeHtml(quoteMessageStr)
                if (quoteMessage != null) {
                    return Quote(encodedUserId, quoteMessage)
                }

            }
            return null
        }
    }
}
