package com.github.ykrank.androidtools

/**
 * Created by ykrank on 2017/11/5.
 */

object GlobalData {
    lateinit var provider: AppDataProvider
    var recycleViewLoadingId: Int = 0
    var recycleViewErrorId: Int = 0

    /**
     * 初始化全局参数
     */
    fun init(appProvider: AppDataProvider) {
        this.provider = appProvider
        initResource(appProvider)
    }

    private fun initResource(appProvider: AppDataProvider) {
        recycleViewLoadingId = appProvider.recycleViewLoadingImgId
        recycleViewErrorId = appProvider.recycleViewErrorImgId
        if (recycleViewLoadingId == 0) {
            recycleViewLoadingId = R.drawable.recycleview_loading
        }
        if (recycleViewErrorId == 0) {
            recycleViewErrorId = R.drawable.recycleview_error_symbol
        }
    }
}
