package me.ykrank.s1next.data.api.model;

import android.support.annotation.Nullable;

import com.github.ykrank.androidtools.guava.Objects;
import com.github.ykrank.androidtools.ui.adapter.model.SameItem;
import com.github.ykrank.androidtools.util.L;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import me.ykrank.s1next.data.api.model.wrapper.HomeReplyWebWrapper;

/**
 * Created by ykrank on 2017/2/5.
 * User's reply model
 */

public class HomeReply implements SameItem, HomeReplyWebWrapper.HomeReplyItem {
    private String reply;
    //eg forum.php?mod=redirect&goto=findpost&ptid=1220112&pid=34645514
    private String url;

    @Nullable
    public static HomeReply fromHtmlElement(Element element) {
        HomeReply reply = null;
        try {
            Elements tdReplies = element.children();
            if (tdReplies.size() < 1) {
                return null;
            }
            Element tdReply = tdReplies.first();
            Elements elesReply = tdReply.children();
            if (elesReply.size() < 3) {
                return null;
            }
            reply = new HomeReply();
            //reply
            Element eleReply = elesReply.get(1);
            reply.setReply(eleReply.text());
            //eg thread-1220112-1-1.html
            reply.setUrl(eleReply.attr("href"));
        } catch (Exception e) {
            L.report(new RuntimeException(element.toString(), e));
        }
        return reply;
    }

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
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
        if (o == null || getClass() != o.getClass()) return false;
        HomeReply homeReply = (HomeReply) o;
        return Objects.equal(reply, homeReply.reply) &&
                Objects.equal(url, homeReply.url);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(reply, url);
    }

    @Override
    public String toString() {
        return "HomeReply{" +
                "reply='" + reply + '\'' +
                ", url='" + url + '\'' +
                '}';
    }

    @Override
    public boolean isSameItem(Object o) {
        if (this == o) return true;
        if (!(o instanceof HomeReply)) return false;
        HomeReply that = (HomeReply) o;
        return Objects.equal(url, that.url);
    }
}
