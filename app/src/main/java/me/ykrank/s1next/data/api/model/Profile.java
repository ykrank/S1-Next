package me.ykrank.s1next.data.api.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * Created by ykrank on 2017/1/8.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public final class Profile extends Account {

    private String name;
    private String uid;
    private String groupTitle;

    public Profile() {

    }

    @JsonCreator
    public Profile(@JsonProperty("extcredits") JsonNode extCredits, @JsonProperty("space") JsonNode space) {
        this.name = space.get("username").asText();
        this.uid = space.get("uid").asText();
        this.groupTitle = space.get("group").get("grouptitle").asText();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getUid() {
        return uid;
    }

    @Override
    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getGroupTitle() {
        return groupTitle;
    }

    public void setGroupTitle(String groupTitle) {
        this.groupTitle = groupTitle;
    }
}
