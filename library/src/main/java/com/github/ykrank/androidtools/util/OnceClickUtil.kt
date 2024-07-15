package com.github.ykrank.androidtools.util

import android.view.View

/**
 * Created by AdminYkrank on 2016/4/17.
 */
object OnceClickUtil {

    /**
     * 设置有抖动的点击事件
     *
     * @param view
     * @return
     */
    fun setClickLister(view: View, clickListener: View.OnClickListener, millDuration: Int = 500) {
        view.setOnClickListener(OnceClickLister(clickListener, millDuration))
    }

    private class OnceClickLister(
        private val lister: View.OnClickListener,
        private val millDuration: Int
    ) : View.OnClickListener {
        private var lastTime = 0L
        override fun onClick(v: View) {
            if (System.currentTimeMillis() - lastTime >= millDuration) {
                lastTime = System.currentTimeMillis()
                lister.onClick(v)
            }
        }
    }
}
