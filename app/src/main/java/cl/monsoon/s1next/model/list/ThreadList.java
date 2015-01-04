package cl.monsoon.s1next.model.list;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import cl.monsoon.s1next.model.Account;
import cl.monsoon.s1next.model.Thread;

/**
 * {@link #data}:
 * <pre>
 * 帖子1
 * 帖子2
 * </pre>
 */
@SuppressWarnings("UnusedDeclaration")
@JsonIgnoreProperties(ignoreUnknown = true)
public final class ThreadList extends Account {

    @JsonProperty("forum_threadlist")
    private List<Thread> data;

    @JsonProperty("forum")
    private Thread.ThreadListInfo threadsInfo;

    public List<Thread> getData() {
        return data;
    }

    public void setData(List<Thread> data) {
        this.data = data;
    }

    public Thread.ThreadListInfo getThreadsInfo() {
        return threadsInfo;
    }

    public void setThreadsInfo(Thread.ThreadListInfo threadsInfo) {
        this.threadsInfo = threadsInfo;
    }
}
