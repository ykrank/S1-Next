package me.ykrank.s1next.data.event;

public final class PmGroupClickEvent {

    private final String toUid;
    private final String toUsername;

    public PmGroupClickEvent(String toUid, String toUsername) {
        this.toUid = toUid;
        this.toUsername = toUsername;
    }

    public String getToUid() {
        return toUid;
    }

    public String getToUsername() {
        return toUsername;
    }
}
