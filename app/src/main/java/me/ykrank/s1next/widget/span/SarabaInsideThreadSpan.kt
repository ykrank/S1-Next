package me.ykrank.s1next.widget.span

import android.net.Uri
import android.view.View
import me.ykrank.s1next.data.api.Api
import me.ykrank.s1next.data.api.model.link.ThreadLink
import me.ykrank.s1next.data.api.model.link.UserLink

/**
 * <xmp>
 * eg:[我](forum.php?mod=viewthread&tid=1340561&highlight=我)
</xmp> *
 * <br></br>
 * Created by ykrank on 2016/10/22 0016.
 */
class SarabaInsideThreadSpan : SarabaSpan() {
    private var threadLink: ThreadLink? = null
    private var userLink: UserLink? = null

    override fun isMatch(uri: Uri): Boolean {
        if (uri.scheme == null && uri.host == null) {
            val url = uri.toString()
            threadLink = ThreadLink.parse(url)
            if (threadLink != null) {
                return true
            }
            userLink = UserLink.parse(url)
            if (userLink != null) {
                return true
            }
        }
        return false
    }

    override fun onClick(uri: Uri, view: View) {
        val path = uri.toString()
        goSaraba(view.context, Uri.parse(Api.BASE_URL + path), threadLink, userLink)
    }
}
