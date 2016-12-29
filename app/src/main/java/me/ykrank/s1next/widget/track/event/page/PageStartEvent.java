package me.ykrank.s1next.widget.track.event.page;

import android.content.Context;

import me.ykrank.s1next.widget.track.event.TrackEvent;

/**
 * Created by ykrank on 2016/12/28.
 * 页面打开事件
 */

public class PageStartEvent extends TrackEvent {
    private Context context;
    private String pageName;

    public PageStartEvent(Context context, String pageName) {
        this.context = context;
        this.pageName = pageName;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public String getPageName() {
        return pageName;
    }

    public void setPageName(String pageName) {
        this.pageName = pageName;
    }
}
