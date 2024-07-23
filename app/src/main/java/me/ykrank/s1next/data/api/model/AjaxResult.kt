package me.ykrank.s1next.data.api.model

/**
 * Created by ykrank on 6/26/24
 * 
 */
data class AjaxResult(
    var success: Boolean,
    var msg: String
) {

    companion object {
        const val SINGLE_QUOTE = "'"
        fun fromAjaxString(html: String?): AjaxResult {
            if (html.isNullOrEmpty()) {
                return AjaxResult(false, "无数据！")
            }
            if (html.contains("succeedhandle_")) {
                val groups = "succeedhandle_.*\\((.*)\\)".toRegex().find(html)?.groupValues
                val paramsStr = groups?.getOrNull(1)
                val params = paramsStr?.split(",")
                // 从参数中按顺序取
                var msg = params?.getOrNull(1)?.trim()
                if (msg?.startsWith(SINGLE_QUOTE) == true) {
                    msg = msg.replaceFirst(SINGLE_QUOTE, "")
                }
                if (msg?.endsWith(SINGLE_QUOTE) == true) {
                    msg = msg.substring(0, msg.length - 1)
                }
                return AjaxResult(true, msg ?: "请求成功!")
            } else {
                val groups = "errorhandle_.*\\((.*)\\)".toRegex().find(html)?.groupValues
                val paramsStr = groups?.getOrNull(1)
                val params = paramsStr?.split(",")
                // 从参数中按顺序取
                var msg = params?.getOrNull(0)?.trim()
                if (msg?.startsWith(SINGLE_QUOTE) == true) {
                    msg = msg.replaceFirst(SINGLE_QUOTE, "")
                }
                if (msg?.endsWith(SINGLE_QUOTE) == true) {
                    msg = msg.substring(0, msg.length - 1)
                }
                return AjaxResult(false, msg ?: html)
            }
        }
    }
}