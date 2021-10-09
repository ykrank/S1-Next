package com.github.ykrank.androidtools.widget.uglyfix

import android.content.Context
import androidx.drawerlayout.widget.DrawerLayout
import android.util.AttributeSet
import android.view.MotionEvent
import java.lang.Exception

/**
 * Created by ykrank on 2017/7/22.
 */

class FixDrawerLayout : androidx.drawerlayout.widget.DrawerLayout {
    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)

    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        try {
            return super.onTouchEvent(ev)
        } catch (e: Exception) {
            e.printStackTrace()
            return true
        }
    }
}
