package cl.monsoon.s1next.model;

import android.text.Html;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Ambiguity in naming due to java.lang.Thread.
 */
@SuppressWarnings("UnusedDeclaration")
@JsonIgnoreProperties(ignoreUnknown = true)
public final class Thread {

    @JsonProperty("tid")
    private String id;

    @JsonProperty("subject")
    private String title;

    @JsonProperty("replies")
    private int replies;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        // encode HTML entities
        this.title = String.valueOf(Html.fromHtml(title));
    }

    public int getReplies() {
        return replies;
    }

    public void setReplies(int replies) {
        this.replies = replies;
    }

    /**
     * Forum info.
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ThreadListInfo {

        @JsonProperty("threads")
        private int threads;

        public int getThreads() {
            return threads;
        }

        public void setThreads(int threads) {
            this.threads = threads;
        }
    }
}
