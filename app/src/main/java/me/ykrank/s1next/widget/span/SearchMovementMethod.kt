package me.ykrank.s1next.widget.span

import android.net.Uri
import android.text.method.MovementMethod
import android.view.View
import me.ykrank.s1next.data.api.Api

/**
 * A movement method that provides selection and clicking on links,
 * also invokes [ImageClickableResizeSpan]'s clicking event.
 */
object SearchMovementMethod {

    @JvmStatic
    val instance: MovementMethod by lazy {
        PostMovementMethod().apply {
            addURLSpanClick(SarabaSpan())
            addURLSpanClick(BilibiliSpan())
            addURLSpanClick(SarabaInsideThreadSpan())
            addURLSpanClick(DefaultSearchURLSpanClick())
        }
    }

    class DefaultSearchURLSpanClick : PostMovementMethod.DefaultURLSpanClick() {
        override fun onClick(uri: Uri, view: View) {
            val realUri = uri.let {
                if (uri.scheme == null && uri.host == null) {
                    Uri.parse(Api.BASE_URL + uri.toString())
                } else {
                    uri
                }
            }
            super.onClick(realUri, view)
        }
    }
}
