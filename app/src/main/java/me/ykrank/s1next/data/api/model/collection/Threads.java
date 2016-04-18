package me.ykrank.s1next.data.api.model.collection;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Objects;

import java.util.ArrayList;
import java.util.List;

import me.ykrank.s1next.data.api.model.Account;
import me.ykrank.s1next.data.api.model.Forum;
import me.ykrank.s1next.data.api.model.Thread;
import me.ykrank.s1next.data.db.BlackListDbWrapper;
import me.ykrank.s1next.data.db.dbmodel.BlackList;

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
    
    public static List<Thread> getFilterThreadList(final List<Thread> oThreads) {
        List<Thread> threads = new ArrayList<>();
        for (Thread thread:oThreads) {
            Thread fThread = getFilterThread(thread);
            if (fThread != null){
                threads.add(fThread);
            }
        }
        return threads;
    }

    public static Thread getFilterThread(final Thread oThread) {
        Thread nThread = oThread;
        BlackListDbWrapper blackListWrapper = BlackListDbWrapper.getInstance();
        switch (blackListWrapper.getForumFlag(oThread.getAuthorid(), oThread.getAuthor())){
            case BlackList.DEL_FORUM:
                nThread = null;
                break;
            case BlackList.HIDE_FORUM:
                if (!oThread.isHide()){
                    nThread = oThread.clone();
                    nThread.setHide(true);
                }
                break;
            default:
                if (oThread.isHide()){
                    nThread = oThread.clone();
                    nThread.setHide(false);
                }
        }
        return nThread;
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
