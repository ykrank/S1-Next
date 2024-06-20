package me.ykrank.s1next.data.api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Objects;

import java.util.List;

import me.ykrank.s1next.util.HtmlUtils;

@SuppressWarnings("UnusedDeclaration")
@JsonIgnoreProperties(ignoreUnknown = true)
public final class ForumCategoryByIds {

    @JsonProperty("name")
    private String name;

    @JsonProperty("forums")
    private List<Integer> forumIds;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = HtmlUtils.INSTANCE.unescapeHtml(name);
    }

    public List<Integer> getForumIds() {
        return forumIds;
    }

    public void setForumIds(List<Integer> forumIDs) {
        this.forumIds = forumIDs;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ForumCategoryByIds that = (ForumCategoryByIds) o;
        return Objects.equal(name, that.name) &&
                Objects.equal(forumIds, that.forumIds);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name, forumIds);
    }
}
