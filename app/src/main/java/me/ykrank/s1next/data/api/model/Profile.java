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
    private int friends;
    private int threads;
    private int replies;
    private String signHtml;
    private int onlineHour;
    private String regDate;

    public Profile() {

    }

    @JsonCreator
    public Profile(@JsonProperty("extcredits") JsonNode extCredits, @JsonProperty("space") JsonNode space) {
        this.name = space.get("username").asText();
        this.uid = space.get("uid").asText();
        this.groupTitle = space.get("group").get("grouptitle").asText();
        this.friends = space.get("friends").asInt();
        int posts = space.get("posts").asInt();
        this.threads = space.get("threads").asInt();
        this.replies = posts - threads;
        this.signHtml = space.get("sightml").asText();
        this.onlineHour = space.get("oltime").asInt();
        this.regDate = space.get("regdate").asText();
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

    public int getFriends() {
        return friends;
    }

    public void setFriends(int friends) {
        this.friends = friends;
    }

    public int getThreads() {
        return threads;
    }

    public void setThreads(int threads) {
        this.threads = threads;
    }

    public int getReplies() {
        return replies;
    }

    public void setReplies(int replies) {
        this.replies = replies;
    }

    public String getSignHtml() {
        return signHtml;
    }

    public void setSignHtml(String signHtml) {
        this.signHtml = signHtml;
    }

    public int getOnlineHour() {
        return onlineHour;
    }

    public void setOnlineHour(int onlineHour) {
        this.onlineHour = onlineHour;
    }

    public String getRegDate() {
        return regDate;
    }

    public void setRegDate(String regDate) {
        this.regDate = regDate;
    }
}
