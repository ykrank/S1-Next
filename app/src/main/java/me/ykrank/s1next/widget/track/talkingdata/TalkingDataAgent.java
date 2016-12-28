package me.ykrank.s1next.widget.track.talkingdata;

import com.tendcloud.tenddata.TCAgent;

import me.ykrank.s1next.App;
import me.ykrank.s1next.BuildConfig;
import me.ykrank.s1next.data.User;

/**
 * Created by ykrank on 2016/12/28.
 * Agent for talking data proxy
 */

public class TalkingDataAgent {

    public static void init() {
        TCAgent.LOG_ON = BuildConfig.DEBUG;
        TCAgent.init(App.get());
        TCAgent.setReportUncaughtExceptions(false);
    }

    public static void reportError(Throwable throwable) {
        TCAgent.onError(App.get(), throwable);
    }

    public static void setUser(User user) {
        TCAgent.setGlobalKV("UserName", user.getName());
        TCAgent.setGlobalKV("Uid", user.getUid());
        TCAgent.setGlobalKV("Permission", user.getPermission());
    }
}
