package me.ykrank.s1next.data.api.model.collection;

import android.support.annotation.Nullable;
import android.support.v4.util.ArrayMap;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.ykrank.androidtools.guava.Objects;
import com.github.ykrank.androidtools.util.L;
import com.github.ykrank.androidtools.util.LooperUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import me.ykrank.s1next.data.api.model.Account;
import me.ykrank.s1next.data.api.model.Forum;
import me.ykrank.s1next.data.api.model.Thread;
import me.ykrank.s1next.data.api.model.ThreadType;
import me.ykrank.s1next.data.db.BlackListDbWrapper;
import me.ykrank.s1next.data.db.ThreadDbWrapper;
import me.ykrank.s1next.data.db.dbmodel.BlackList;
import me.ykrank.s1next.data.db.dbmodel.DbThread;

@SuppressWarnings("UnusedDeclaration")
@JsonIgnoreProperties(ignoreUnknown = true)
public final class Threads extends Account {

    @JsonProperty("forum")
    private Thread.ThreadListInfo threadListInfo;

    @JsonIgnore
    private List<Thread> threadList;

    @JsonProperty("sublist")
    private List<Forum> subForumList;

    @JsonIgnore
    private ArrayList<ThreadType> threadTypes;

    public Threads() {
    }

    @JsonCreator
    public Threads(@JsonProperty("threadtypes") JsonNode typesNode,
                   @JsonProperty("forum_threadlist") List<Thread> threadList) {
        ArrayList<ThreadType> threadTypes = new ArrayList<>();
        try {
            ArrayMap<String, String> typeMap = new ArrayMap<>();
            Iterator<Map.Entry<String, JsonNode>> fields = typesNode.get("types").fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> entry = fields.next();
                ThreadType type = new ThreadType(entry.getKey(), entry.getValue().asText());
                threadTypes.add(type);
                typeMap.put(type.getTypeId(), type.getTypeName());
            }
            if (threadList != null) {
                for (Thread thread : threadList) {
                    thread.setTypeName(typeMap.get(thread.getTypeId()));
                }
            }
        } catch (Exception e) {
            L.report(e);
        }
        this.threadTypes = threadTypes;
        this.threadList = getFilterThreadList(threadList);
    }

    /**
     * @see #getFilterThread(Thread)
     */
    public static List<Thread> getFilterThreadList(final List<Thread> oThreads) {
        List<Thread> threads = new ArrayList<>();
        for (Thread thread : oThreads) {
            Thread fThread = getFilterThread(thread);
            if (fThread != null) {
                threads.add(fThread);
            }
        }
        return threads;
    }

    /**
     * 对数据源进行处理
     * <ul>
     * <li>获取上次访问时回复数</li>
     * </ul>
     * 如果修改了过滤状态，则会返回不同的对象
     */
    @Nullable
    public static Thread getFilterThread(final Thread oThread) {
        LooperUtil.enforceOnWorkThread();
        Thread nThread = oThread;
        BlackListDbWrapper blackListWrapper = BlackListDbWrapper.getInstance();
        switch (blackListWrapper.getForumFlag(oThread.getAuthorId(), oThread.getAuthor())) {
            case BlackList.DEL_FORUM:
                nThread = null;
                break;
            case BlackList.HIDE_FORUM:
                if (!oThread.isHide()) {
                    nThread = oThread.clone();
                    nThread.setHide(true);
                }
                break;
            case BlackList.NORMAL:
            default:
                if (oThread.isHide()) {
                    nThread = oThread.clone();
                    nThread.setHide(false);
                }
        }
        if (nThread != null) {
            DbThread dbThread = ThreadDbWrapper.getInstance().getWithThreadId(Integer.valueOf(nThread.getId()));
            if (dbThread != null) {
                nThread.setLastReplyCount(dbThread.getLastCountWhenView());
            }
        }
        return nThread;
    }

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

    public ArrayList<ThreadType> getThreadTypes() {
        return threadTypes;
    }

    public void setThreadTypes(ArrayList<ThreadType> threadTypes) {
        this.threadTypes = threadTypes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Threads threads = (Threads) o;
        return Objects.equal(threadListInfo, threads.threadListInfo) &&
                Objects.equal(threadList, threads.threadList) &&
                Objects.equal(subForumList, threads.subForumList) &&
                Objects.equal(threadTypes, threads.threadTypes);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(super.hashCode(), threadListInfo, threadList, subForumList, threadTypes);
    }
}
