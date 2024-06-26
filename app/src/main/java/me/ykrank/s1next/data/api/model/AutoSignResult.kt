package me.ykrank.s1next.data.api.model

/**
 * Created by ykrank on 2017/6/4.
 */

class AutoSignResult {
    var success: Boolean = false
    var msg: String? = null
    var signed: Boolean = false

    companion object {
        fun fromHtml(html: String): AutoSignResult {
            val result = AjaxResult.fromAjaxString(html)
            return AutoSignResult().apply {
                success = result.success
                msg = result.msg
                signed = result.msg.contains("已签到")
            }
        }
    }
}