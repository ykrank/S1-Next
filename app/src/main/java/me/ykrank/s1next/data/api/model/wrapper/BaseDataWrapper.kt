package me.ykrank.s1next.data.api.model.wrapper

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import me.ykrank.s1next.data.api.model.Account

@JsonIgnoreProperties(ignoreUnknown = true)
open class BaseDataWrapper<T : Account> : OriginWrapper<T>()
