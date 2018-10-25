package me.ykrank.s1next.viewmodel

import android.databinding.ObservableField
import android.view.View

import com.github.ykrank.androidtools.widget.RxBus

import me.ykrank.s1next.data.api.model.Emoticon
import me.ykrank.s1next.view.event.EmoticonClickEvent

class EmoticonViewModel {

    val emoticon = ObservableField<Emoticon>()

    fun clickEmotion(rxBus: RxBus): View.OnClickListener {
        return View.OnClickListener { view ->
            emoticon.get()?.let {
                // notify ReplyFragment that emoticon had been clicked
                rxBus.post(EmoticonClickEvent(it.entity))
            }
        }
    }
}
