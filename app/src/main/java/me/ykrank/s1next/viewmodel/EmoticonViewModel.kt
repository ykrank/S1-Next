package me.ykrank.s1next.viewmodel

import androidx.databinding.ObservableField
import android.view.View

import com.github.ykrank.androidtools.widget.EventBus

import me.ykrank.s1next.data.api.model.Emoticon
import me.ykrank.s1next.view.event.EmoticonClickEvent

class EmoticonViewModel {

    val emoticon = ObservableField<Emoticon>()

    fun clickEmotion(eventBus: EventBus): View.OnClickListener {
        return View.OnClickListener { view ->
            emoticon.get()?.let {
                // notify ReplyFragment that emoticon had been clicked
                eventBus.postDefault(EmoticonClickEvent(it.entity))
            }
        }
    }
}
