package me.ykrank.s1next.widget.span

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Browser
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ClickableSpan
import android.view.View
import com.github.ykrank.androidtools.util.L.report
import me.ykrank.s1next.widget.span.PostMovementMethod.URLSpanClick
import org.xml.sax.Attributes

/**
 * Created by ykrank on 2016/10/16 0016.
 */
class AcfunSpan : URLSpanClick {
    override fun isMatch(uri: Uri): Boolean {
        return false
    }

    override fun onClick(uri: Uri, view: View) {
        goAcfun(view.context, uri)
    }

    private class AcfunHref(var mHref: String?)
    private class AcfunURLSpan(val url: String?) : ClickableSpan() {

        override fun onClick(widget: View) {
            goAcfun(widget.context, Uri.parse(url))
        }
    }

    companion object {
        /**
         * See android.text.HtmlToSpannedConverter#startA(android.text.SpannableStringBuilder, org.xml.sax.Attributes)
         */
        @JvmStatic
        fun startAcfun(text: SpannableStringBuilder, attributes: Attributes?) {
            val href = attributes?.getValue("href")
            val len = text.length
            text.setSpan(AcfunHref(href), len, len, Spannable.SPAN_MARK_MARK)
        }

        /**
         * See android.text.HtmlToSpannedConverter#endA(android.text.SpannableStringBuilder)
         */
        @JvmStatic
        fun endAcfun(text: SpannableStringBuilder) {
            val len = text.length
            val obj: Any? = TagHandler.getLastSpan(text, AcfunHref::class.java)
            val where = text.getSpanStart(obj)
            text.removeSpan(obj)
            if (where != len && obj != null) {
                val h = obj as AcfunHref
                if (h.mHref != null) {
                    text.setSpan(
                        AcfunURLSpan(h.mHref), where, len,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
            }
        }

        // 对Acfun链接进行独立处理，调用acfun客户端
        //TODO 目前Acfun客户端似乎不支持直接intent-filter调用
        private fun goAcfun(context: Context, uri: Uri) {
            val intent = Intent(Intent.ACTION_VIEW, uri)
            intent.putExtra(Browser.EXTRA_APPLICATION_ID, context.packageName)
            try {
                context.startActivity(intent)
            } catch (e: Throwable) {
                report("AcfunURLSpan, Acfun Actvity was not found for intent, $intent", e)
            }
        }
    }
}
