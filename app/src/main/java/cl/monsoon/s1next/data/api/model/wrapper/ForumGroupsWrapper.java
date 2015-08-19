package cl.monsoon.s1next.data.api.model.wrapper;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Objects;

import cl.monsoon.s1next.data.api.model.collection.ForumGroups;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class ForumGroupsWrapper {

    @JsonProperty("Variables")
    private ForumGroups forumGroups;

    public ForumGroups getForumGroups() {
        return forumGroups;
    }

    @SuppressWarnings("UnusedDeclaration")
    public void setForumGroups(ForumGroups forumGroups) {
        this.forumGroups = forumGroups;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ForumGroupsWrapper that = (ForumGroupsWrapper) o;
        return Objects.equal(forumGroups, that.forumGroups);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(forumGroups);
    }
}
