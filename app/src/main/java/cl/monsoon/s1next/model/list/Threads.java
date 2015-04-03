package cl.monsoon.s1next.model.list;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import cl.monsoon.s1next.model.Account;
import cl.monsoon.s1next.model.Forum;
import cl.monsoon.s1next.model.Thread;

/**
 * {@link #threadList}:
 * <pre>
 * 帖子1
 * 帖子2
 * </pre>
 */
@SuppressWarnings("UnusedDeclaration")
@JsonIgnoreProperties(ignoreUnknown = true)
public final class Threads extends Account {

    @JsonProperty("forum")
    private Thread.ThreadListInfo threadListInfo;

    @JsonProperty("forum_threadlist")
    private List<Thread> threadList;

    @JsonProperty("sublist")
    private List<Forum> subForumList;

    public Thread.ThreadListInfo getThreadListInfo() {
        return threadListInfo;
    }

    public void setThreadListInfo(Thread.ThreadListInfo threadListInfo) {
        this.threadListInfo = threadListInfo;
    }

    public List<Thread> getThreadList() {
        return threadList;
    }

    public void setThreadList(List<Thread> threadList) {
        this.threadList = threadList;
    }

    public List<Forum> getSubForumList() {
        return subForumList;
    }

    public void setSubForumList(List<Forum> subForumList) {
        this.subForumList = subForumList;
    }
}
