package me.ykrank.s1next.view.event;

import me.ykrank.s1next.data.api.model.PmGroup;

public final class PmGroupClickEvent {

    private final PmGroup pmGroup;

    public PmGroupClickEvent(PmGroup pmGroup) {
        this.pmGroup = pmGroup;
    }

    public PmGroup getPmGroup() {
        return pmGroup;
    }
}
