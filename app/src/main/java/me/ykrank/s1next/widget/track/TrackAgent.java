package me.ykrank.s1next.widget.track;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Map;

import me.ykrank.s1next.data.User;

/**
 * Created by ykrank on 2016/12/29.
 */

public interface TrackAgent {
    void init();

    void setUser(@Nullable User user);

    void onResume(@NonNull Activity activity);

    void onPause(@NonNull Activity activity);

    void onPageStart(@NonNull Context context, String string);

    void onPageEnd(@NonNull Context context, String string);

    void onEvent(@NonNull Context context, String name, String label, Map<String, String> data);
}
