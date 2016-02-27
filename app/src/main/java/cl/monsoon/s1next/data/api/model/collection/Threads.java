package cl.monsoon.s1next.data.api.model.collection;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

import java.util.ArrayList;
import java.util.List;

import cl.monsoon.s1next.data.api.model.Account;
import cl.monsoon.s1next.data.api.model.Forum;
import cl.monsoon.s1next.data.api.model.Thread;
import cl.monsoon.s1next.data.db.BlackListDbWrapper;
import cl.monsoon.s1next.data.db.dbmodel.BlackList;

@SuppressWarnings("UnusedDeclaration")
@JsonIgnoreProperties(ignoreUnknown = true)
public final class Threads extends Account {
    
    BlackListDbWrapper blackListWrapper = BlackListDbWrapper.getInstance();

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
    
    public List<Thread> getFilterThreadList(List<Thread> oThreads) {
        Preconditions.checkNotNull(blackListWrapper);
        List<Thread> threads = new ArrayList<>();
        for (Thread thread:oThreads) {
            switch (blackListWrapper.getForumFlag(thread.getAuthorid(), thread.getAuthor())){
                case BlackList.DEL_FORUM:
                    break;
                case BlackList.HIDE_FORUM:
                    thread.setHide(true);
                    threads.add(thread);
                    break;
                default:
                    threads.add(thread);
            }
        }
        return threads;
    }

    public void setThreadList(List<Thread> threadList) {
        this.threadList = getFilterThreadList(threadList);
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
