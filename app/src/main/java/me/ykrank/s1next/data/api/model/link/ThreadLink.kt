package me.ykrank.s1next.data.api.model.link

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ThreadLink(
    val threadId: String,
    val jumpPage: Int,
    val quotePostId: String?
) : Parcelable {

    private constructor(builder: Builder) : this(
        builder.threadId,
        builder.jumpPage,
        builder.quotePostId
    )

    class Builder(val threadId: String) {
        var jumpPage = 1
        var quotePostId: String? = null

        fun jumpPage(jumpPage: Int): Builder {
            this.jumpPage = jumpPage
            return this
        }

        fun quotePostId(quotePostId: String?): Builder {
            this.quotePostId = quotePostId
            return this
        }

        fun build(): ThreadLink {
            return ThreadLink(this)
        }
    }

    companion object {
        /**
         * Parses thread link in order to get the meta info for this thread.
         *
         * @param url The thread link.
         * @return The `Optional.of(threadLink)` if we parse this thread
         * link/ID successfully, otherwise the `Optional.absent()`.
         */
        @JvmStatic
        fun parse(url: String): ThreadLink? {
            // example: http://bbs.saraba1st.com/2b/forum.php?mod=redirect&goto=findpost&pid=27217893&ptid=1074030
            val ptid = "ptid=(\\d+)".toRegex().find(url)?.groupValues?.getOrNull(1)
            if (!ptid.isNullOrEmpty()) {
                val builder = Builder(ptid)
                val pid = "pid=(\\d+)".toRegex().find(url)?.groupValues?.getOrNull(1)
                builder.quotePostId(pid)
                return builder.build()
            }

            // example: http://bbs.saraba1st.com/2b/thread-1074030-1-1.html
            var groupValues = "thread-(\\d+)-(\\d+)".toRegex().find(url)?.groupValues
            if (groupValues != null && groupValues.size > 2) {
                return Builder(groupValues[1])
                    .jumpPage(groupValues[2].toIntOrNull() ?: 1)
                    .build()
            }

            // example:
            // http://bbs.saraba1st.com/2b/forum.php?mod=viewthread&tid=1074030
            // or http://bbs.saraba1st.com/2b/archiver/tid-1074030.html
            groupValues = "tid([=\\-])(\\d+)".toRegex().find(url)?.groupValues
            if (groupValues != null && groupValues.size > 2) {
                val builder = Builder(groupValues[2])

                // example: http://bbs.saraba1st.com/2b/forum.php?mod=viewthread&tid=1074030&page=7
                // or http://bbs.saraba1st.com/2b/archiver/tid-1074030.html?page=7
                val page =
                    "page=(\\d+)".toRegex().find(url)?.groupValues?.getOrNull(1)?.toIntOrNull()
                if (page != null) {
                    builder.jumpPage(page)
                }
                return builder.build()
            }
            return null
        }

        /**
         * Parses thread link/ID in order to get the meta info for this thread.
         *
         * @param threadLinkOrId The thread link/ID.
         * @return The `Optional.of(threadLink)` if we parse this thread
         * link/ID successfully, otherwise the `Optional.absent()`.
         */
        fun parse2(threadLinkOrId: String): ThreadLink? {
            // example: 1074030-1
            var groupValues = "^(\\d+)-(\\d+)$".toRegex().find(threadLinkOrId)?.groupValues
            if (groupValues != null && groupValues.size > 2) {
                val builder = Builder(groupValues[1])
                    .jumpPage(groupValues[2].toIntOrNull() ?: 1)
                return builder.build()
            }

            // example: 1074030
            groupValues = "^(\\d+)$".toRegex().find(threadLinkOrId)?.groupValues
            if (groupValues != null && groupValues.size > 1) {
                val builder = Builder(groupValues[1])
                return builder.build()
            }
            return parse(threadLinkOrId)
        }
    }
}
