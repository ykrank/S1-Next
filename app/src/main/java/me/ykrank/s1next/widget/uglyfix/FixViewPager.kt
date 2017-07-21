package me.ykrank.s1next.widget.uglyfix

import android.content.Context
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.view.MotionEvent
import java.lang.Exception

/**
 * Viewpager sometimes throw java.lang.IllegalArgumentException pointerIndex out of range in 6.0 (api 23)
 */
class FixViewPager : ViewPager {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        try {
            return super.onInterceptTouchEvent(ev)
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }
}
