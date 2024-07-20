package me.ykrank.s1next.viewmodel

import androidx.databinding.Observable
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import me.ykrank.s1next.data.db.dbmodel.BlackWord

class BlackWordViewModel {
    val blackword: ObservableField<BlackWord> = ObservableField()

    val loading = ObservableBoolean()

    val message = ObservableField<String>()

    val floatVisible = ObservableBoolean()

    init {
        val floatVisibleCallback = object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                refreshFloatVisible()
            }
        }
        loading.addOnPropertyChangedCallback(floatVisibleCallback)

        message.addOnPropertyChangedCallback(floatVisibleCallback)
    }

    private fun refreshFloatVisible() {
        floatVisible.set(loading.get() || !message.get().isNullOrEmpty())
    }
}
