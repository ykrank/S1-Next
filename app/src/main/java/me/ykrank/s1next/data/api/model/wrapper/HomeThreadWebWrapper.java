package me.ykrank.s1next.data.api.model.wrapper;

import android.support.annotation.NonNull;

import com.github.ykrank.androidtools.util.L;
import com.google.common.base.Objects;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import me.ykrank.s1next.data.api.model.HomeThread;

/**
 * Created by ykrank on 2017/2/4.
 */

public class HomeThreadWebWrapper {
    private List<HomeThread> threads;
    private boolean more;

    @NonNull
    public static HomeThreadWebWrapper fromHtml(String html) {
        HomeThreadWebWrapper wrapper = new HomeThreadWebWrapper();
        List<HomeThread> threads = new ArrayList<>();
        try {
            Document document = Jsoup.parse(html);
            HtmlDataWrapper.Companion.preTreatHtml(document);
            Elements elements = document.select("#delform tr");
            for (int i = 1; i < elements.size(); i++) {
                HomeThread homeThread = HomeThread.fromHtmlElement(elements.get(i));
                if (homeThread != null) {
                    threads.add(homeThread);
                }
            }
            //more
            Elements next = document.select("div.pg>a.nxt");
            wrapper.setMore(!next.isEmpty());
        } catch (Exception e) {
            L.report(e);
        }
        wrapper.setThreads(threads);
        return wrapper;
    }

    public List<HomeThread> getThreads() {
        return threads;
    }

    public void setThreads(List<HomeThread> threads) {
        this.threads = threads;
    }

    public boolean isMore() {
        return more;
    }

    public void setMore(boolean more) {
        this.more = more;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof HomeThreadWebWrapper)) return false;
        HomeThreadWebWrapper that = (HomeThreadWebWrapper) o;
        return more == that.more &&
                Objects.equal(threads, that.threads);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(threads, more);
    }

    @Override
    public String toString() {
        return "HomeThreadWebWrapper{" +
                "threads=" + threads +
                ", more=" + more +
                '}';
    }
}
