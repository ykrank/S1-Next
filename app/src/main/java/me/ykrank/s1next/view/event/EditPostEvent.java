package me.ykrank.s1next.view.event;

import me.ykrank.s1next.data.api.model.Post;
import me.ykrank.s1next.data.api.model.Thread;

public final class EditPostEvent {

    private final Post post;
    private final Thread thread;

    public EditPostEvent(Post post, Thread thread) {
        this.post = post;
        this.thread = thread;
    }

    public Post getPost() {
        return post;
    }

    public Thread getThread() {
        return thread;
    }
}
