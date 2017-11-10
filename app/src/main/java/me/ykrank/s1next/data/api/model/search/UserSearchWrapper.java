package me.ykrank.s1next.data.api.model.search;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.github.ykrank.androidtools.util.L;
import com.google.common.base.Objects;
import com.google.common.base.Optional;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import me.ykrank.s1next.data.api.model.UserLink;
import me.ykrank.s1next.data.api.model.wrapper.HtmlDataWrapper;

/**
 * Created by ykrank on 2017/04/01.
 */

public class UserSearchWrapper {
    @NonNull
    private List<UserSearchResult> userSearchResults = new ArrayList<>();
    @Nullable
    private String errorMsg;

    public static UserSearchWrapper fromSource(String source) {
        UserSearchWrapper wrapper = new UserSearchWrapper();
        List<UserSearchResult> userSearchResults = new ArrayList<>();
        try {
            Document document = Jsoup.parse(source);
            HtmlDataWrapper.Companion.preTreatHtml(document);
            Elements errorElements = document.select("div#messagetext");
            if (errorElements.size() > 0) {
                wrapper.setErrorMsg(errorElements.text());
            }
            //count
            Elements elements = document.select("li.bbda.cl");
            for (int i = 0; i < elements.size(); i++) {
                Element element = elements.get(i);
                try {
                    UserSearchResult result = new UserSearchResult();
                    Element userElement = element.child(1).child(0);
                    //uid
                    String href = userElement.attr("href");
                    Optional<UserLink> userLink = UserLink.parse(href);
                    if (!userLink.isPresent()) {
                        throw new IllegalStateException("Could not parse uid from link");
                    }
                    result.setUid(userLink.get().getUid());
                    //name
                    String name = userElement.text();
                    result.setName(name);
                    userSearchResults.add(result);
                } catch (Exception e) {
                    L.leaveMsg("Element:" + element.html());
                    L.report(e);
                }
            }
        } catch (Exception e) {
            L.leaveMsg("Source:" + source);
            L.report(e);
        }
        wrapper.setUserSearchResults(userSearchResults);
        return wrapper;
    }

    @NonNull
    public List<UserSearchResult> getUserSearchResults() {
        return userSearchResults;
    }

    public void setUserSearchResults(@NonNull List<UserSearchResult> userSearchResults) {
        this.userSearchResults = userSearchResults;
    }

    @Nullable
    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(@Nullable String errorMsg) {
        this.errorMsg = errorMsg;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserSearchWrapper)) return false;
        UserSearchWrapper that = (UserSearchWrapper) o;
        return Objects.equal(userSearchResults, that.userSearchResults) &&
                Objects.equal(errorMsg, that.errorMsg);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(userSearchResults, errorMsg);
    }

    @Override
    public String toString() {
        return "UserSearchWrapper{" +
                "userSearchResults=" + userSearchResults +
                ", errorMsg='" + errorMsg + '\'' +
                '}';
    }
}
