package me.ykrank.s1next.viewmodel;

import android.databinding.ObservableField;
import android.view.View;

import me.ykrank.s1next.data.api.model.Emoticon;
import me.ykrank.s1next.view.event.EmoticonClickEvent;
import me.ykrank.s1next.widget.RxBus;

public final class EmoticonViewModel {

    public final ObservableField<Emoticon> emoticon = new ObservableField<>();

    public View.OnClickListener clickEmotion(RxBus rxBus) {
        return v -> {
            // notify ReplyFragment that emoticon had been clicked
            rxBus.post(new EmoticonClickEvent(emoticon.get().getEntity()));
        };
    }
}
