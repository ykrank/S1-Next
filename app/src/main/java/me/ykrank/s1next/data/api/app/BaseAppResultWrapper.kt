package me.ykrank.s1next.data.api.app

/**
 * Created by ykrank on 2017/7/22.
 */
open class BaseAppResultWrapper<D> {
    var code: Int = 0
    var message: String? = null
    var success: Boolean = false
    var data: D? = null
}