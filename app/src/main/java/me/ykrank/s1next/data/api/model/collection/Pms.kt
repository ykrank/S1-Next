package me.ykrank.s1next.data.api.model.collection

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import me.ykrank.s1next.data.api.model.Pm

@JsonIgnoreProperties(ignoreUnknown = true)
class Pms : BaseAccountCollection<Pm>() {

}
