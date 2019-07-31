package me.ykrank.s1next.widget.track.event;

import androidx.annotation.Nullable;

import com.github.ykrank.androidtools.widget.track.event.TrackEvent;

/**
 * Created by ykrank on 2016/12/29.
 */

public class NewRateTrackEvent extends TrackEvent {

    public NewRateTrackEvent(String threadId, @Nullable String quotePostId, String score, String reason) {
        setGroup("新评分");
        addData("ThreadId", threadId);
        addData("QuotePostId", quotePostId);
        addData("Score", score);
        addData("Reason", reason);
    }
}
