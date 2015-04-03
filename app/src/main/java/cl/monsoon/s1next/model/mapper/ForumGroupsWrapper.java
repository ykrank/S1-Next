package cl.monsoon.s1next.model.mapper;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import cl.monsoon.s1next.model.list.ForumGroups;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class ForumGroupsWrapper implements Deserializable {

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
