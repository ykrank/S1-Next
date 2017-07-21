package me.ykrank.s1next.widget.hostcheck;

import android.os.SystemClock;

import me.ykrank.s1next.data.User;
import me.ykrank.s1next.data.api.S1Service;
import me.ykrank.s1next.util.L;
import me.ykrank.s1next.util.RxJavaUtil;
import me.ykrank.s1next.view.event.NoticeRefreshEvent;
import me.ykrank.s1next.widget.EventBus;

public class NoticeCheckTask {
    private static final int periodic = 300_000;

    private final EventBus mEventBus;
    private final S1Service mS1Service;
    private final User mUser;

    private volatile long lastCheckTime;
    private volatile boolean checking = false;

    public NoticeCheckTask(EventBus eventBus, S1Service s1Service, User user) {
        this.mEventBus = eventBus;
        this.mS1Service = s1Service;
        this.mUser = user;
    }

    public void inspectCheckNoticeTask() {
        if (checking || !mUser.isLogged()) {
            return;
        }
        if (lastCheckTime == 0 || SystemClock.elapsedRealtime() - lastCheckTime > periodic) {
            startCheckNotice();
        }
    }

    public void forceCheckNotice() {
        if (checking || !mUser.isLogged()) {
            return;
        }
        startCheckNotice();
    }

    private void startCheckNotice() {
        checking = true;
        mS1Service.getPmGroups(1)
                .compose(RxJavaUtil.iOTransformer())
                .doOnTerminate(() -> lastCheckTime = SystemClock.elapsedRealtime())
                .subscribe(wrapper -> {
                    mEventBus.post(NoticeRefreshEvent.class, new NoticeRefreshEvent(wrapper.getData().hasNew(), null));
                }, L::e);
    }
}
