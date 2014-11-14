package cl.monsoon.s1next.model.list;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collections;
import java.util.List;

import cl.monsoon.s1next.model.Account;
import cl.monsoon.s1next.model.Thread;

/**
 * {@link #threadList}:
 * <pre>
 * &#x5e16;&#x5b50;1
 * &#x5e16;&#x5b50;2
 * </pre>
 */
@SuppressWarnings("UnusedDeclaration")
@JsonIgnoreProperties(ignoreUnknown = true)
public final class ThreadList extends Account {

    @JsonProperty("forum_threadlist")
    private List<Thread> threadList;

    @JsonProperty("forum")
    private Thread.ThreadListInfo threadsInfo;

    public List<Thread> getThreadList() {
        return Collections.unmodifiableList(threadList);
    }

    public void setThreadList(List<Thread> threadList) {
        this.threadList = threadList;
    }

    public Thread.ThreadListInfo getThreadsInfo() {
        return threadsInfo;
    }

    public void setThreadsInfo(Thread.ThreadListInfo threadsInfo) {
        this.threadsInfo = threadsInfo;
    }
}
