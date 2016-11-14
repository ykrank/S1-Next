package me.ykrank.s1next.viewmodel;

import android.databinding.ObservableField;
import android.view.View;

import me.ykrank.s1next.data.api.model.PmGroup;
import me.ykrank.s1next.data.event.PmGroupClickEvent;
import me.ykrank.s1next.widget.EventBus;


public final class PmGroupViewModel {

    public final ObservableField<PmGroup> pmGroup = new ObservableField<>();

    public View.OnClickListener clickGroup(EventBus eventBus){
        return v -> eventBus.post(new PmGroupClickEvent(pmGroup.get()));
    }
}
