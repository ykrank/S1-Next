package me.ykrank.s1next.widget.track.event;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ykrank on 2016/12/27.
 */
public class TrackEvent {

    @Nullable
    private String group;

    @Nullable
    private String name;

    @Nullable
    private Map<String, String> data;

    protected TrackEvent() {
        
    }

    public TrackEvent(@Nullable String group, @Nullable String name, @Nullable Map<String, String> data) {
        this.group = group;
        this.name = name;
        this.data = data;
    }

    @Nullable
    public String getGroup() {
        return group;
    }

    public void setGroup(@Nullable String group) {
        this.group = group;
    }

    @Nullable
    public String getName() {
        return name;
    }

    public void setName(@Nullable String name) {
        this.name = name;
    }

    @Nullable
    public Map<String, String> getData() {
        return data;
    }

    public void addData(String key, String value) {
        if (data == null) {
            data = new HashMap<>();
        }
        data.put(key, value);
    }

    public void setData(@Nullable Map<String, String> data) {
        this.data = data;
    }

    /**
     * use to verify handler
     */
    @NonNull
    public Class getEventType() {
        return this.getClass();
    }

    ;
}
