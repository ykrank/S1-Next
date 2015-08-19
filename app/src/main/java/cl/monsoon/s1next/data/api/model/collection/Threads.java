package cl.monsoon.s1next.data.api.model.collection;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Objects;

import java.util.List;

import cl.monsoon.s1next.data.api.model.Account;
import cl.monsoon.s1next.data.api.model.Forum;
import cl.monsoon.s1next.data.api.model.Thread;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Threads threads = (Threads) o;
        return Objects.equal(threadListInfo, threads.threadListInfo) &&
                Objects.equal(threadList, threads.threadList) &&
                Objects.equal(subForumList, threads.subForumList);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(super.hashCode(), threadListInfo, threadList, subForumList);
    }
}
