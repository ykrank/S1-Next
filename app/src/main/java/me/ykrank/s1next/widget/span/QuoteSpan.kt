package me.ykrank.s1next.widget.span

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.text.Layout
import android.text.SpannableString
import android.text.Spanned
import android.text.style.LeadingMarginSpan
import android.text.style.QuoteSpan
import com.github.ykrank.androidtools.util.ResourceUtil
import me.ykrank.s1next.R

class QuoteSpan(private val stripeColor: Int, private val stripeWidth: Float, private val gap: Float) : LeadingMarginSpan {

    override fun drawLeadingMargin(c: Canvas?, p: Paint?, x: Int, dir: Int, top: Int, baseline: Int, bottom: Int, text: CharSequence?, start: Int, end: Int, first: Boolean, layout: Layout?) {
        p ?: return
        c ?: return
        val style = p.style
        val paintColor = p.color
        p.style = Paint.Style.FILL
        p.color = stripeColor
        c.drawRect(x.toFloat(), top.toFloat(), x + dir * stripeWidth, bottom.toFloat(), p)
        p.style = style
        p.color = paintColor
        return
    }

    override fun getLeadingMargin(first: Boolean): Int {
        return (stripeWidth + gap).toInt()
    }
}

fun Spanned.replaceQuoteSpans(context: Context): Spanned {
    val quoteSpans = getSpans(0, length, QuoteSpan::class.java)
    var spannableString = SpannableString(this)
    for (quoteSpan in quoteSpans) {
        val start = getSpanStart(quoteSpan)
        val end = getSpanEnd(quoteSpan)
        val flags = getSpanFlags(quoteSpan)
        spannableString.removeSpan(quoteSpan)
        spannableString.setSpan(QuoteSpan(
                ResourceUtil.getAttrColorInt(context, androidx.appcompat.R.attr.colorPrimaryDark),
                10f,
                15f),
                start,
                end,
                flags)
    }
    return spannableString
}