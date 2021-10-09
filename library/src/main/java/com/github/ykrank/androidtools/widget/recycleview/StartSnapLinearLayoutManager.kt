package com.github.ykrank.androidtools.widget.recycleview

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import android.util.AttributeSet

/**LinearLayoutManager only snap to start
 * Created by ykrank on 2017/6/4.
 */
class StartSnapLinearLayoutManager : androidx.recyclerview.widget.LinearLayoutManager {
    val context: Context

    constructor(context: Context) : super(context) {
        this.context = context
    }

    constructor(context: Context, orientation: Int, reverseLayout: Boolean) : super(context, orientation, reverseLayout) {
        this.context = context
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        this.context = context
    }

    /**
     * Smooth scroll to the specified adapter position
     */
    fun smoothScrollToPosition(position: Int) {
        smoothScrollToPosition(position, 0)
    }

    /**
     * Smooth scroll to the specified adapter position with offset at start
     */
    fun smoothScrollToPosition(position: Int, offset: Int) {
        val scroller = StartSnapLinearSmoothScroller(context)
        scroller.targetPosition = position
        scroller.offset = offset
        startSmoothScroll(scroller)
    }
}

class StartSnapLinearSmoothScroller(context: Context?) : androidx.recyclerview.widget.LinearSmoothScroller(context) {
    var offset: Int = 0

    override fun getVerticalSnapPreference(): Int {
        return SNAP_TO_START
    }

    override fun getHorizontalSnapPreference(): Int {
        return SNAP_TO_START
    }

    override fun calculateDtToFit(viewStart: Int, viewEnd: Int, boxStart: Int, boxEnd: Int, snapPreference: Int): Int {
        //snapPreference always SNAP_TO_START
        if (snapPreference == SNAP_TO_START) {
            return boxStart - viewStart + offset
        }
        throw IllegalArgumentException("snap preference should be SNAP_TO_START")
    }
}