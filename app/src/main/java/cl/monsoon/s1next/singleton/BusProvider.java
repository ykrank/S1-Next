package cl.monsoon.s1next.singleton;

import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

public enum BusProvider {
    INSTANCE;

    private final Bus bus;

    BusProvider() {
        bus = new Bus(ThreadEnforcer.ANY);
    }

    public static Bus get() {
        return INSTANCE.bus;
    }
}
