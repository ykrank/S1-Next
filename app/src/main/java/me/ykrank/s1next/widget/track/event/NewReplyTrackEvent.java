package me.ykrank.s1next.widget.track.event;

import androidx.annotation.Nullable;

import com.github.ykrank.androidtools.widget.track.event.TrackEvent;

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
