package me.ykrank.s1next.data.api.app.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Created by ykrank on 2017/7/22.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
open class AppListWrapper<D> : AppDataWrapper<BaseAppList<D>>()

@JsonIgnoreProperties(ignoreUnknown = true)
class BaseAppList<D> {
    @JsonProperty("pageNo")
    var pageNo: Int = 0
    @JsonProperty("pageSize")
    var pageSize: Int = 0
    @JsonProperty("totalCount")
    var totalCount: Int = 0
    @JsonProperty("webpageurl")
    var webPageUrl: String? = null
    @JsonProperty("list")
    var list: ArrayList<D> = arrayListOf()
}