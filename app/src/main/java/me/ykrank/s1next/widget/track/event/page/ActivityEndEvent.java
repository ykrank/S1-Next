package me.ykrank.s1next.widget.track.event.page;

import android.app.Activity;

import me.ykrank.s1next.widget.track.event.TrackEvent;

/**
 * Created by ykrank on 2016/12/28.
 * Activity结束事件
 */

public class ActivityEndEvent extends TrackEvent {
    private Activity activity;

    public ActivityEndEvent(Activity activity) {
        this.activity = activity;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }
}
