package me.ykrank.s1next.widget.span

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.provider.Browser
import android.text.Selection
import android.text.Spannable
import android.text.method.ArrowKeyMovementMethod
import android.text.method.MovementMethod
import android.text.style.ClickableSpan
import android.text.style.URLSpan
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.github.ykrank.androidtools.util.L.e
import com.github.ykrank.androidtools.util.L.report
import me.ykrank.s1next.R
import me.ykrank.s1next.util.IntentUtil.putCustomTabsExtra

/**
 * A movement method that provides selection and clicking on links,
 * also invokes [ImageClickableResizeSpan]'s clicking event.
 */
open class PostMovementMethod(private val defaultURLSpanClick: URLSpanClick = DefaultURLSpanClick()) :
    ArrowKeyMovementMethod() {
    private val urlSpanClicks: MutableList<URLSpanClick> = ArrayList()

    /**
     * @see android.text.method.LinkMovementMethod.onTouchEvent
     */
    override fun onTouchEvent(widget: TextView, buffer: Spannable, event: MotionEvent): Boolean {
        val action = event.action

        if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_DOWN) {
            var x = event.x.toInt()
            var y = event.y.toInt()

            x -= widget.totalPaddingLeft
            y -= widget.totalPaddingTop

            x += widget.scrollX
            y += widget.scrollY

            val layout = widget.layout
            val line = layout.getLineForVertical(y)
            val off = layout.getOffsetForHorizontal(line, x.toFloat())

            val link = buffer.getSpans(off, off, ClickableSpan::class.java)

            if (link.size != 0) {
                if (action == MotionEvent.ACTION_UP) {
                    val imageClickableSpans = buffer.getSpans(
                        off, off,
                        ImageClickableResizeSpan::class.java
                    )
                    if (imageClickableSpans.size != 0) {
                        val context = widget.context
                        val dialog = AlertDialog.Builder(context)
                            .setItems(
                                arrayOf<CharSequence>(
                                    context.getString(R.string.show_image),
                                    context.getString(R.string.go_url)
                                )
                            ) { dialog, which ->
                                when (which) {
                                    0 -> showImage(widget, buffer, line, off, imageClickableSpans)
                                    1 -> goUrl(widget, link)
                                }
                            }.create()
                        dialog.show()
                    } else {
                        goUrl(widget, link)
                    }
                } else {
                    //http://stackoverflow.com/questions/15836306/can-a-textview-be-selectable-and-contain-links
                    //error: Error when selecting text from Textview (java.lang.IndexOutOfBoundsException: setSpan (-1 ... -1) starts before 0)
                    if (!isSelecting(buffer)) {
                        Selection.setSelection(
                            buffer,
                            buffer.getSpanStart(link[0]),
                            buffer.getSpanEnd(link[0])
                        )
                    }
                }
                return true
            }

            // invoke ImageClickableResizeSpan's clicking event
            val imageClickableSpans = buffer.getSpans(
                off, off,
                ImageClickableResizeSpan::class.java
            )
            if (imageClickableSpans.size != 0) {
                if (action == MotionEvent.ACTION_UP) {
                    showImage(widget, buffer, line, off, imageClickableSpans)
                } else {
                    if (!isSelecting(buffer)) {
                        Selection.setSelection(
                            buffer,
                            buffer.getSpanStart(imageClickableSpans[0]),
                            buffer.getSpanEnd(imageClickableSpans[0])
                        )
                    }
                }
                return true
            }
        }

        return super.onTouchEvent(widget, buffer, event)
    }

    private fun showImage(
        widget: TextView, buffer: Spannable, line: Int, off: Int,
        imageClickableSpans: Array<ImageClickableResizeSpan>
    ) {
        if (imageClickableSpans.size > 1) {
            //if use getSpans(off, off , sometime click mid in two span will cause error
            val spans = buffer.getSpans(
                off, off + 1,
                ImageClickableResizeSpan::class.java
            )
            if (spans.size == 1) {
                spans[0].onClick(widget)
                return
            }
            report(
                IllegalStateException(
                    """
    ImageClickableResizeSpan length warn; 
    length${imageClickableSpans.size},line:$line,off:$off,newLength:${spans.size}
    """.trimIndent()
                )
            )
        }
        imageClickableSpans[0].onClick(widget)
        return
    }

    private fun goUrl(widget: TextView, link: Array<ClickableSpan>) {
        val clickableSpan = link[0]
        if (clickableSpan is URLSpan) {
            val uri = Uri.parse(clickableSpan.url)
            for (click in urlSpanClicks) {
                if (click.isMatch(uri)) {
                    click.onClick(uri, widget)
                    return
                }
            }
            defaultURLSpanClick.onClick(uri, widget)
        } else {
            clickableSpan.onClick(widget)
        }
        return
    }

    fun addURLSpanClick(click: URLSpanClick) {
        if (!urlSpanClicks.contains(click)) {
            urlSpanClicks.add(click)
        }
    }

    interface URLSpanClick {
        /**
         * whether uri use this click listener
         */
        fun isMatch(uri: Uri): Boolean

        fun onClick(uri: Uri, view: View)
    }

    open class DefaultURLSpanClick : URLSpanClick {
        override fun isMatch(uri: Uri): Boolean {
            return true
        }

        override fun onClick(uri: Uri, view: View) {
            // support Custom Tabs for URLSpan
            // see URLSpan#onClick(View)
            val context = view.context
            val intent = Intent(Intent.ACTION_VIEW, uri)
            intent.putExtra(Browser.EXTRA_APPLICATION_ID, context.packageName)
            putCustomTabsExtra(intent)
            try {
                context.startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                e("URLSpan", "Activity was not found for intent, $intent")
            }
        }
    }

    companion object {

        @JvmStatic
        val instance by lazy {
            PostMovementMethod().apply {
                addURLSpanClick(SarabaSpan())
                addURLSpanClick(BilibiliSpan())
            }
        }

        private fun isSelecting(buffer: Spannable): Boolean {
            return Selection.getSelectionStart(buffer) != -1 && Selection.getSelectionEnd(buffer) != -1
        }
    }
}
