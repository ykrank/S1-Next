package cl.monsoon.s1next.data.api.model.wrapper;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

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
}
