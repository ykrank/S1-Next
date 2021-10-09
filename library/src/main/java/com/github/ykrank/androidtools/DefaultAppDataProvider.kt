package com.github.ykrank.androidtools

import com.github.ykrank.androidtools.util.DefaultErrorParser
import com.github.ykrank.androidtools.util.ErrorParser

/**
 * Created by ykrank on 2017/11/6.
 */
abstract class DefaultAppDataProvider : AppDataProvider {
    override val errorParser: ErrorParser?
        get() = DefaultErrorParser
    override val itemModelBRid: Int
        get() = BR.model
    override val recycleViewLoadingImgId: Int
        get() = R.drawable.loading
    override val recycleViewErrorImgId: Int
        get() = R.drawable.recycleview_error_symbol
}