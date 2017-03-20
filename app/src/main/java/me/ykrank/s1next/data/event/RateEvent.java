package me.ykrank.s1next.data.event;

public final class RateEvent {

    private final String threadId, postId;

    public RateEvent(String threadId, String postId) {
        this.threadId = threadId;
        this.postId = postId;
    }

    public String getThreadId() {
        return threadId;
    }

    public String getPostId() {
        return postId;
    }
}
