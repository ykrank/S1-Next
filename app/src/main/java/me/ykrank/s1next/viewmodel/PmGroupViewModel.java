package me.ykrank.s1next.viewmodel;

import androidx.databinding.ObservableField;
import android.view.View;

import com.github.ykrank.androidtools.widget.EventBus;

import me.ykrank.s1next.data.api.model.PmGroup;
import me.ykrank.s1next.view.event.PmGroupClickEvent;


public final class PmGroupViewModel {

    public final ObservableField<PmGroup> pmGroup = new ObservableField<>();

    public View.OnClickListener clickGroup(EventBus eventBus) {
        return v -> eventBus.postDefault(new PmGroupClickEvent(pmGroup.get()));
    }
}
