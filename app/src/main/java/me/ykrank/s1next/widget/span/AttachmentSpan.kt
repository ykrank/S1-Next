package me.ykrank.s1next.widget.span

import android.content.Context
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ClickableSpan
import android.view.MenuItem
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.github.ykrank.androidtools.util.ContextUtils
import com.github.ykrank.androidtools.util.L
import com.github.ykrank.androidtools.util.LooperUtil
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.ykrank.s1next.App
import me.ykrank.s1next.R
import me.ykrank.s1next.util.AppFileUtil
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.Request
import okhttp3.coroutines.executeAsync
import okio.buffer
import okio.sink
import org.xml.sax.Attributes

class AttachmentSpan {
    private class AttachmentHref(val href: String?, val name: String?)
    private class AttachmentURLSpan(val span: AttachmentHref) : ClickableSpan() {

        override fun onClick(widget: View) {
            go(widget, span)
        }
    }

    companion object {
        /**
         * See android.text.HtmlToSpannedConverter#startA(android.text.SpannableStringBuilder, org.xml.sax.Attributes)
         */
        @JvmStatic
        fun startSpan(text: SpannableStringBuilder, attributes: Attributes?) {
            val href = attributes?.getValue("href")
            val name = attributes?.getValue("name")
            val len = text.length
            text.setSpan(AttachmentHref(href, name), len, len, Spannable.SPAN_MARK_MARK)
        }

        /**
         * See android.text.HtmlToSpannedConverter#endA(android.text.SpannableStringBuilder)
         */
        @JvmStatic
        fun endSpan(text: SpannableStringBuilder) {
            val len = text.length
            val obj: Any? = TagHandler.getLastSpan(text, AttachmentHref::class.java)
            val where = text.getSpanStart(obj)
            text.removeSpan(obj)
            if (where != len && obj != null) {
                val h = obj as AttachmentHref
                if (h.href != null) {
                    text.setSpan(
                        AttachmentURLSpan(h), where, len,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
            }
        }

        private fun go(v: View, span: AttachmentHref) {
            val popup = PopupMenu(v.context, v)
            popup.setOnMenuItemClickListener { menuitem: MenuItem ->
                val activity = ContextUtils.getBaseContext(v.context) as? FragmentActivity
                activity?.supportFragmentManager?.apply {
                    download(activity, this, span)
                }
                true
            }
            popup.menu.add(R.string.menu_download)
            popup.show()
        }

        @OptIn(DelicateCoroutinesApi::class, ExperimentalCoroutinesApi::class)
        private fun download(context: Context, fm: FragmentManager, span: AttachmentHref) {
            val httpUrl = span.href?.toHttpUrlOrNull() ?: return

            AppFileUtil.getDownloadPath(fm, { uri ->
                val fileName = span.name ?: span.href
                val extension = MimeTypeMap.getFileExtensionFromUrl(fileName)
                val type =
                    MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension) ?: "image/*"
                val file = uri.createFile(type, fileName)
                file?.uri?.also { fileUri ->
                    fun onError(e: Throwable) {
                        L.e(e)
                        LooperUtil.postToMainThread {
                            Toast.makeText(
                                context,
                                R.string.download_unknown_error,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                    GlobalScope.launch(
                        CoroutineExceptionHandler { _, e ->
                            onError(e)
                        }
                    ) {
                        val okHttpClient = App.appComponent.imageOkHttpClient
                        val call = okHttpClient.newCall(Request(httpUrl))
                        call.executeAsync().use { resp ->
                            if (!resp.isSuccessful) {
                                onError(IllegalStateException(resp.message))
                                return@use
                            }
                            withContext(Dispatchers.IO) {
                                context.contentResolver.openOutputStream(fileUri)!!.use {
                                    it.sink().buffer().writeAll(resp.body.source())
                                }
                            }

                            LooperUtil.postToMainThread {
                                Toast.makeText(
                                    context,
                                    R.string.download_success,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                }
            })
        }
    }
}
