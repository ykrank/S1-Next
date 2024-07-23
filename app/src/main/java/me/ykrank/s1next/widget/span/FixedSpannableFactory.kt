package me.ykrank.s1next.widget.span

import android.text.Spannable

/**
 * Created by ykrank on 2021/10/9
 * https://developer.android.google.cn/guide/topics/text/spans?hl=zh-cn#set-text-multiple
 */
class FixedSpannableFactory : Spannable.Factory() {

    override fun newSpannable(source: CharSequence?): Spannable {
        if (source is Spannable) {
            return source
        }
        return super.newSpannable(source)
    }
}