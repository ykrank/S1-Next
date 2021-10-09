package com.github.ykrank.androidtools

import com.github.ykrank.androidtools.util.ErrorParser

/**
 * Created by ykrank on 2017/11/6.
 */
interface AppDataProvider {
    val errorParser: ErrorParser?
    val logTag: String
    val debug: Boolean
    val buildType: String
    /**
     * recycleView的item中的主数据model的BR id
     */
    val itemModelBRid: Int
    val recycleViewLoadingImgId: Int
    val recycleViewErrorImgId: Int
    /**
     * R文件的class
     */
    val appR: Class<out Any>
}