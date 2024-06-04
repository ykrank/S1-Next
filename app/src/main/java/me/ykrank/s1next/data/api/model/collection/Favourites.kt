package me.ykrank.s1next.data.api.model.collection

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import me.ykrank.s1next.data.api.model.Favourite

@JsonIgnoreProperties(ignoreUnknown = true)
class Favourites : BaseAccountCollection<Favourite>() {

}
