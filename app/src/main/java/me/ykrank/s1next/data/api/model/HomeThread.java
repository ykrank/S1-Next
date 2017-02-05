package me.ykrank.s1next.data.api.model;

import android.support.annotation.Nullable;

import com.google.common.base.Objects;

import org.jsoup.nodes.Element;

import me.ykrank.s1next.data.SameItem;
import me.ykrank.s1next.data.api.model.wrapper.HomeReplyWebWrapper;
import me.ykrank.s1next.util.L;

/**
 * Created by ykrank on 2017/2/4.
 * User's thread model
 */

public class HomeThread implements SameItem, HomeReplyWebWrapper.HomeReplyItem {
    private String title;
    private String forum;
    private String view;
    private String reply;
    private String lastReplier;
    private String lastReplyDate;
    //eg thread-1220112-1-1.html
    private String url;

    @Nullable
    public static HomeThread fromHtmlElement(Element element) {
        HomeThread thread = null;
        try {
            if (element.children().size() < 5) {
                return null;
            }
            thread = new HomeThread();
            //title
            Element title = element.child(1).child(0);
            thread.setTitle(title.text());
            //eg thread-1220112-1-1.html
            thread.setUrl(title.attr("href"));
            //forum
            Element forum = element.child(2).child(0);
            thread.setForum(forum.text());
            //num
            Element num = element.child(3);
            thread.setReply(num.child(0).text());
            thread.setView(num.child(1).text());
            //by
            Element by = element.child(4);
            thread.setLastReplier(by.child(0).child(0).text());
            thread.setLastReplyDate(by.child(1).child(0).text());
        } catch (Exception e) {
            L.report(e);
        }
        return thread;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getForum() {
        return forum;
    }

    public void setForum(String forum) {
        this.forum = forum;
    }

    public String getView() {
        return view;
    }

    public void setView(String view) {
        this.view = view;
    }

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }

    public String getLastReplier() {
        return lastReplier;
    }

    public void setLastReplier(String lastReplier) {
        this.lastReplier = lastReplier;
    }

    public String getLastReplyDate() {
        return lastReplyDate;
    }

    public void setLastReplyDate(String lastReplyDate) {
        this.lastReplyDate = lastReplyDate;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof HomeThread)) return false;
        HomeThread thread = (HomeThread) o;
        return Objects.equal(title, thread.title) &&
                Objects.equal(forum, thread.forum) &&
                Objects.equal(view, thread.view) &&
                Objects.equal(reply, thread.reply) &&
                Objects.equal(lastReplier, thread.lastReplier) &&
                Objects.equal(lastReplyDate, thread.lastReplyDate) &&
                Objects.equal(url, thread.url);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(title, forum, view, reply, lastReplier, lastReplyDate, url);
    }

    @Override
    public String toString() {
        return "HomeThread{" +
                "title='" + title + '\'' +
                ", forum='" + forum + '\'' +
                ", view='" + view + '\'' +
                ", reply='" + reply + '\'' +
                ", lastReplier='" + lastReplier + '\'' +
                ", lastReplyDate='" + lastReplyDate + '\'' +
                ", url='" + url + '\'' +
                '}';
    }

    @Override
    public boolean isSameItem(Object o) {
        if (this == o) return true;
        if (!(o instanceof HomeThread)) return false;
        HomeThread that = (HomeThread) o;
        return Objects.equal(url, that.url);
    }
}
