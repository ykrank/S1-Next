package com.github.ykrank.androidtools.widget.uglyfix

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager

/**
 * Viewpager sometimes throw java.lang.IllegalArgumentException pointerIndex out of range in 6.0 (api 23)
 */
class FixViewPager : ViewPager {
    var pagingEnabled: Boolean = true

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        return pagingEnabled && super.onTouchEvent(ev)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        try {
            return pagingEnabled && super.onInterceptTouchEvent(ev)
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }
}
