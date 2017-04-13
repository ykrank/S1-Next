package me.ykrank.s1next.widget;

import android.os.SystemClock;

import javax.inject.Inject;

import me.ykrank.s1next.App;
import me.ykrank.s1next.data.api.S1Service;
import me.ykrank.s1next.data.event.NoticeRefreshEvent;
import me.ykrank.s1next.util.L;
import me.ykrank.s1next.util.RxJavaUtil;

/**
 * Created by ykrank on 2017/4/13.
 */

public class NoticeCheckTask {
    public static final int periodic = 300_000;

    @Inject
    EventBus mEventBus;
    @Inject
    S1Service mS1Service;

    private volatile long lastCheckTime;
    private volatile boolean checking = false;

    public NoticeCheckTask() {
        App.getAppComponent().inject(this);
    }

    public void inspectCheckNoticeTask() {
        if (checking) {
            return;
        }
        if (lastCheckTime == 0 || SystemClock.elapsedRealtime() - lastCheckTime > periodic) {
            startCheckNotice();
        }
    }

    private void startCheckNotice() {
        checking = true;
        mS1Service.getPmGroups(1)
                .compose(RxJavaUtil.iOTransformer())
                .doOnTerminate(() -> lastCheckTime = SystemClock.elapsedRealtime())
                .subscribe(wrapper -> {
                    mEventBus.post(new NoticeRefreshEvent(wrapper.getData().hasNew(), false));
                }, L::e);
    }
}
