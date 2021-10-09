package com.github.ykrank.androidtools.widget.track.event.page;

import android.content.Context;

import com.github.ykrank.androidtools.widget.track.event.TrackEvent;

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

    public String getPageName() {
        return pageName;
    }
}
