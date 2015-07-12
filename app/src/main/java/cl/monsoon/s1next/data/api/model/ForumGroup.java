package cl.monsoon.s1next.data.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.apache.commons.lang3.StringEscapeUtils;

import java.util.List;

@SuppressWarnings("UnusedDeclaration")
@JsonIgnoreProperties(ignoreUnknown = true)
public final class ForumGroup {

    @JsonProperty("name")
    private String name;

    @JsonProperty("forums")
    private List<Integer> forumIds;

    @JsonIgnore
    private List<Forum> forumList;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        // unescape some basic XML entities
        this.name = StringEscapeUtils.unescapeXml(name);
    }

    public List<Integer> getForumIds() {
        return forumIds;
    }

    public void setForumIds(List<Integer> forumIDs) {
        this.forumIds = forumIDs;
    }

    public List<Forum> getForumList() {
        return forumList;
    }

    public void setForumList(List<Forum> forumList) {
        this.forumList = forumList;
    }
}
