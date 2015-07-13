package cl.monsoon.s1next.data;

public final class Wifi {

    private volatile boolean wifiEnabled;

    public boolean isWifiEnabled() {
        return wifiEnabled;
    }

    public void setWifiEnabled(boolean wifiEnabled) {
        this.wifiEnabled = wifiEnabled;
    }
}
