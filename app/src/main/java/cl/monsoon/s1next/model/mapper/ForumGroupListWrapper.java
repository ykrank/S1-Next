package cl.monsoon.s1next.model.mapper;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import cl.monsoon.s1next.model.list.ForumGroupList;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class ForumGroupListWrapper implements Deserializable {

    @JsonProperty("Variables")
    private ForumGroupList data;

    public ForumGroupList unwrap() {
        return data;
    }

    @SuppressWarnings("UnusedDeclaration")
    public void setData(ForumGroupList data) {
        this.data = data;
    }
}
