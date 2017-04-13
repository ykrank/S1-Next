package me.ykrank.s1next.data.event;

/**
 * Created by ykrank on 2017/4/13.
 */

public class NoticeRefreshEvent {
    private boolean newPm;
    private boolean newNotice;

    public NoticeRefreshEvent(boolean newPm, boolean newNotice) {
        this.newPm = newPm;
        this.newNotice = newNotice;
    }

    public boolean isNewPm() {
        return newPm;
    }

    public boolean isNewNotice() {
        return newNotice;
    }
}
