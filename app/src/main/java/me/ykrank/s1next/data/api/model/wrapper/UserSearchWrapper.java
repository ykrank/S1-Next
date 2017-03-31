package me.ykrank.s1next.data.api.model.wrapper;

import android.support.annotation.NonNull;

import com.google.common.base.Objects;
import com.google.common.base.Optional;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import me.ykrank.s1next.data.api.model.UserLink;
import me.ykrank.s1next.data.api.model.UserSearchResult;
import me.ykrank.s1next.util.L;

/**
 * Created by ykrank on 2017/04/01.
 */

public class UserSearchWrapper {
    @NonNull
    private List<UserSearchResult> userSearchResults = new ArrayList<>();

    public static UserSearchWrapper fromSource(String source) {
        UserSearchWrapper wrapper = new UserSearchWrapper();
        List<UserSearchResult> userSearchResults = new ArrayList<>();
        try {
            Document document = Jsoup.parse(source);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserSearchWrapper that = (UserSearchWrapper) o;
        return Objects.equal(userSearchResults, that.userSearchResults);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(userSearchResults);
    }

    @Override
    public String toString() {
        return "UserSearchWrapper{" +
                "userSearchResults=" + userSearchResults +
                '}';
    }
}
