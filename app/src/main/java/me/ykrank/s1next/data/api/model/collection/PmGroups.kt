package me.ykrank.s1next.data.api.model.collection

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import me.ykrank.s1next.data.api.model.PmGroup

@JsonIgnoreProperties(ignoreUnknown = true)
class PmGroups : BaseAccountCollection<PmGroup>() {

    fun hasNew(): Boolean {
        list?.apply {
            for (pmGroup in this) {
                if (pmGroup.isNew) {
                    return true
                }
            }
        }
        return false
    }
}
