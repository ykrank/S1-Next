package me.ykrank.s1next.widget.track.event;

import android.support.annotation.Nullable;

/**
 * Created by ykrank on 2016/12/29.
 */

public class NewReplyTrackEvent extends TrackEvent {

    public NewReplyTrackEvent(String threadId, @Nullable String quotePostId) {
        setGroup("新回复");
        addData("ThreadId", threadId);
        addData("QuotePostId", quotePostId);
    }
}
