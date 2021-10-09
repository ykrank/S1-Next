package com.github.ykrank.androidtools.ui

/**
 * Created by ykrank on 2017/11/6.
 */
object UiGlobalData {
    var provider: UiDataProvider? = null
    var R: RProvider? = null
    lateinit var toast: (CharSequence?, Int) -> Unit

    /**
     * 初始化全局参数
     */
    fun init(uiProvider: UiDataProvider?, R: RProvider?, toast: (CharSequence?, Int) -> Unit) {
        this.provider = uiProvider
        this.R = R
        this.toast = toast
    }
}