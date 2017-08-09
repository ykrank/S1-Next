package me.ykrank.s1next.widget.track.talkingdata;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;

import com.tendcloud.tenddata.TCAgent;

import java.util.Map;

import me.ykrank.s1next.App;
import me.ykrank.s1next.data.User;
import me.ykrank.s1next.widget.track.TrackAgent;

/**
 * Created by ykrank on 2016/12/28.
 * Agent for talking data proxy
 */

public class TalkingDataAgent implements TrackAgent {

    public void init() {
        TCAgent.LOG_ON = false;
        TCAgent.init(App.get());
        TCAgent.setReportUncaughtExceptions(false);
    }

    public void setUser(@NonNull User user) {
        TCAgent.setGlobalKV("UserName", user.getName());
        TCAgent.setGlobalKV("Uid", user.getUid());
        TCAgent.setGlobalKV("Permission", String.valueOf(user.getPermission()));
    }

    @Override
    public void onResume(@NonNull Activity activity) {
        TCAgent.onPageStart(activity, activity.getLocalClassName());
    }

    @Override
    public void onPause(@NonNull Activity activity) {
        TCAgent.onPageEnd(activity, activity.getLocalClassName());
    }

    @Override
    public void onPageStart(@NonNull Context context, String string) {
        TCAgent.onPageStart(context, string);
    }

    @Override
    public void onPageEnd(@NonNull Context context, String string) {
        TCAgent.onPageEnd(context, string);
    }

    @Override
    public void onEvent(@NonNull Context context, String name, String label, Map<String, String> data) {
        TCAgent.onEvent(context, name, label, data);
    }
}
