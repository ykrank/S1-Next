package me.ykrank.s1next.data.api.model.search;

import androidx.annotation.NonNull;

import com.github.ykrank.androidtools.util.L;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.ykrank.s1next.data.api.model.wrapper.HtmlDataWrapper;

/**
 * Created by ykrank on 2016/10/21.
 */

public class ForumSearchWrapper {
    private int count;
    private int page;
    private int maxPage;
    @NonNull
    private String href = "";
    @NonNull
    private List<ForumSearchResult> forumSearchResults = new ArrayList<>();

    @NonNull
    public static ForumSearchWrapper fromSource(String source) {
        ForumSearchWrapper wrapper = new ForumSearchWrapper();
        try {
            Document document = Jsoup.parse(source);
            HtmlDataWrapper.Companion.preTreatHtml(document);
            //count
            Elements elements = document.select("em");
            if (elements.isEmpty()) {
                return wrapper;
            }
            String countString = elements.get(0).text();
            Pattern pattern = Pattern.compile("^找到 “.+” 相关内容 (\\d+) 个$");
            Matcher matcher = pattern.matcher(countString);
            if (!matcher.find()) {
                return wrapper;
            }
            int count = Integer.parseInt(matcher.group(1).trim());
            wrapper.setCount(count);
            if (count == 0) {
                return wrapper;
            }

            //results
            List<ForumSearchResult> forumSearchResults = new ArrayList<>();
            elements = document.select("li.pbw");
            ListIterator<Element> iterator = elements.listIterator();
            while (iterator.hasNext()) {
                Element resultEle = iterator.next();
                ForumSearchResult forumSearchResult = new ForumSearchResult();
                forumSearchResult.setContent(resultEle.html());
                forumSearchResults.add(forumSearchResult);
            }
            wrapper.setForumSearchResults(forumSearchResults);

            //page
            elements = document.select("div.pg");
            if (elements.isEmpty()) {
                //only one page
                return wrapper;
            }
            Element element = elements.get(0);
            elements = element.getElementsByTag("strong");
            int page = Integer.parseInt(elements.get(0).text().trim());
            wrapper.setPage(page);

            //maxpage
            elements = element.select("span[title]");
            String maxPageStr = elements.get(0).text();
            int maxPage = Integer.parseInt(maxPageStr.substring(2, maxPageStr.length() - 2).trim());
            wrapper.setMaxPage(maxPage);

            //href
            elements = element.getElementsByTag("a");
            String href = elements.get(0).attr("href").replaceFirst("page=\\d+", "page=");
            wrapper.setHref(href);
        } catch (Exception e) {
            L.report(e);
        }
        return wrapper;
    }


    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    @NonNull
    public List<ForumSearchResult> getForumSearchResults() {
        return forumSearchResults;
    }

    public void setForumSearchResults(@NonNull List<ForumSearchResult> forumSearchResults) {
        this.forumSearchResults = forumSearchResults;
    }

    public int getMaxPage() {
        return maxPage;
    }

    public void setMaxPage(int maxPage) {
        this.maxPage = maxPage;
    }

    @NonNull
    public String getHref() {
        return href;
    }

    public void setHref(@NonNull String href) {
        this.href = href;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ForumSearchWrapper)) return false;

        ForumSearchWrapper wrapper = (ForumSearchWrapper) o;

        if (count != wrapper.count) return false;
        if (page != wrapper.page) return false;
        if (maxPage != wrapper.maxPage) return false;
        if (!href.equals(wrapper.href)) return false;
        return forumSearchResults.equals(wrapper.forumSearchResults);

    }

    @Override
    public int hashCode() {
        int result = count;
        result = 31 * result + page;
        result = 31 * result + maxPage;
        result = 31 * result + href.hashCode();
        result = 31 * result + forumSearchResults.hashCode();
        return result;
    }
}
