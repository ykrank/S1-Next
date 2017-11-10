package me.ykrank.s1next.widget.hostcheck;

import android.os.SystemClock;

import com.github.ykrank.androidtools.util.L;
import com.github.ykrank.androidtools.util.RxJavaUtil;
import com.github.ykrank.androidtools.widget.RxBus;

import me.ykrank.s1next.data.User;
import me.ykrank.s1next.data.api.S1Service;
import me.ykrank.s1next.view.event.NoticeRefreshEvent;

public class NoticeCheckTask {
    private static final int periodic = 300_000;

    private final RxBus mRxBus;
    private final S1Service mS1Service;
    private final User mUser;

    private volatile long lastCheckTime;
    private volatile boolean checking = false;

    public NoticeCheckTask(RxBus rxBus, S1Service s1Service, User user) {
        this.mRxBus = rxBus;
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
                .compose(RxJavaUtil.iOSingleTransformer())
                .doAfterTerminate(() -> lastCheckTime = SystemClock.elapsedRealtime())
                .subscribe(wrapper -> {
                    mRxBus.post(NoticeRefreshEvent.class, new NoticeRefreshEvent(wrapper.getData().hasNew(), null));
                }, L::e);
    }
}
