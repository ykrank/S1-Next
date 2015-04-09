package cl.monsoon.s1next.singleton;

import com.squareup.otto.Bus;

public enum BusProvider {
    INSTANCE;

    private final Bus bus;

    BusProvider() {
        bus = new Bus();
    }

    public static Bus get() {
        return INSTANCE.bus;
    }
}
