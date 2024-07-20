package me.ykrank.s1next.data.api.model.link

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ForumLink(val fid: String, val page: Int) : Parcelable {
    companion object {
        /**
         * Parses user link in order to get the meta info for this user.
         *
         * @param url The user space link.
         * @return The `Optional.of(userLink)` if we parse this user
         * link/ID successfully, otherwise the `Optional.absent()`.
         */
        fun parse(url: String?): ForumLink? {
            if (url.isNullOrEmpty()) return null
            // example: space-uid-223963.html
            val groups = "forum-(\\d+)-(\\d+)".toRegex().find(url)?.groupValues
            if (groups != null && groups.size > 2) {
                return ForumLink(groups[1], groups[2].toInt())
            }
            return null
        }
    }
}
