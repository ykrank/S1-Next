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

    private String homeUsername;
    private String homeUid;
    private String groupTitle;
    private int friends;
    private int threads;
    private int replies;
    private String signHtml;
    private int onlineHour;
    private String regDate;
    private String lastVisitDate;
    private String lastActiveDate;
    private String lastPostDate;
    private int credits;
    private int combatEffectiveness;
    private int gold;
    private int rp;
    private int shameSense;

    public Profile() {

    }

    @JsonCreator
    public Profile(@JsonProperty("extcredits") JsonNode extCredits, @JsonProperty("space") JsonNode space) {
        this.homeUsername = space.get("username").asText();
        this.homeUid = space.get("uid").asText();
        this.groupTitle = space.get("group").get("grouptitle").asText();
        this.friends = space.get("friends").asInt();
        int posts = space.get("posts").asInt();
        this.threads = space.get("threads").asInt();
        this.replies = posts - threads;
        this.signHtml = space.get("sightml").asText();
        this.onlineHour = space.get("oltime").asInt();
        this.regDate = space.get("regdate").asText();
        this.lastVisitDate = space.get("lastvisit").asText();
        this.lastActiveDate = space.get("lastactivity").asText();
        this.lastPostDate = space.get("lastpost").asText();
        this.credits = space.get("credits").asInt();
        this.combatEffectiveness = space.get("extcredits1").asInt();
        this.gold = space.get("extcredits2").asInt();
        this.rp = space.get("extcredits4").asInt();
        this.shameSense = space.get("extcredits7").asInt();
    }

    public String getHomeUsername() {
        return homeUsername;
    }

    public void setHomeUsername(String homeUsername) {
        this.homeUsername = homeUsername;
    }

    public String getHomeUid() {
        return homeUid;
    }

    public void setHomeUid(String homeUid) {
        this.homeUid = homeUid;
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

    public String getLastVisitDate() {
        return lastVisitDate;
    }

    public void setLastVisitDate(String lastVisitDate) {
        this.lastVisitDate = lastVisitDate;
    }

    public String getLastActiveDate() {
        return lastActiveDate;
    }

    public void setLastActiveDate(String lastActiveDate) {
        this.lastActiveDate = lastActiveDate;
    }

    public String getLastPostDate() {
        return lastPostDate;
    }

    public void setLastPostDate(String lastPostDate) {
        this.lastPostDate = lastPostDate;
    }

    public int getCredits() {
        return credits;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }

    public int getCombatEffectiveness() {
        return combatEffectiveness;
    }

    public void setCombatEffectiveness(int combatEffectiveness) {
        this.combatEffectiveness = combatEffectiveness;
    }

    public int getGold() {
        return gold;
    }

    public void setGold(int gold) {
        this.gold = gold;
    }

    public int getRp() {
        return rp;
    }

    public void setRp(int rp) {
        this.rp = rp;
    }

    public int getShameSense() {
        return shameSense;
    }

    public void setShameSense(int shameSense) {
        this.shameSense = shameSense;
    }
}
