package me.ykrank.s1next.widget.span

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.View
import androidx.fragment.app.FragmentActivity
import me.ykrank.s1next.data.api.model.link.ThreadLink
import me.ykrank.s1next.data.api.model.link.UserLink
import me.ykrank.s1next.util.IntentUtil
import me.ykrank.s1next.view.activity.UserHomeActivity
import me.ykrank.s1next.view.page.post.postlist.PostListGatewayActivity
import me.ykrank.s1next.widget.span.PostMovementMethod.URLSpanClick

/**
 * <xmp>
 * 符合intent-filter的<a></a> tag。
</xmp> *
 * <br></br>
 * <xmp>
 * eg:[http://bbs.saraba1st.com/2b/forum-75-1.html](http://bbs.saraba1st.com/2b/forum-75-1.html)
</xmp> *
 * <br></br>
 * Created by ykrank on 2016/10/16 0016.
 */
open class SarabaSpan : URLSpanClick {
    override fun isMatch(uri: Uri): Boolean {
        if ("http".equals(uri.scheme, ignoreCase = true) ||
            "https".equals(uri.scheme, ignoreCase = true)
        ) {
            var path = uri.encodedPath
            if (path == null) {
                path = "/"
            }
            val host = uri.host ?: ""
            for (filter in HOST_FILTERS) {
                val hostFilter = filter.key
                val pathPattern = filter.value

                if (IntentUtil.matchMainHost(host, hostFilter) &&
                    IntentUtil.matchPath(path, pathPattern)
                ) {
                    return true
                }
            }
        }
        return false
    }

    override fun onClick(uri: Uri, view: View) {
        goSaraba(view.context, uri)
    }

    companion object {
        /**
         * intent-filter.从AndroidManifest中提取
         */
        private val HOST_FILTERS: Map<String, Regex> by lazy {
            buildMap {
                put("saraba1st.com", IntentUtil.REGEX_MATCH_ALL_PATH)
                put("stage1.cc", IntentUtil.REGEX_MATCH_ALL_PATH)
                put("stage1st.com", IntentUtil.REGEX_MATCH_ALL_PATH)
            }
        }

        // 对Saraba链接进行独立处理，直接打开界面
        @JvmStatic
        protected fun goSaraba(
            context: Context,
            uri: Uri,
        ) {
            val url = uri.toString()
            val threadLink = ThreadLink.parse(url)
            if (threadLink != null) {
                PostListGatewayActivity.start(context, threadLink)
                return
            }
            val userLink = UserLink.parse(url)
            if (userLink != null) {
                UserHomeActivity.start(context as FragmentActivity, userLink.uid, null)
                return
            }

            context.startActivity(Intent(Intent.ACTION_VIEW, uri))
        }

        @JvmStatic
        protected fun goSaraba(
            context: Context,
            uri: Uri,
            threadLink: ThreadLink? = null,
            userLink: UserLink? = null
        ) {
            if (threadLink != null) {
                PostListGatewayActivity.start(context, threadLink)
                return
            }
            if (userLink != null) {
                UserHomeActivity.start(context as FragmentActivity, userLink.uid, null)
                return
            }

            goSaraba(context, uri)
        }
    }
}
