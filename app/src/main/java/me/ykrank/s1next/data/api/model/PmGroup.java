package me.ykrank.s1next.data.api.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Objects;

import me.ykrank.s1next.data.SameItem;

/**
 * Created by ykrank on 2016/11/12 0012.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public final class PmGroup implements Cloneable, SameItem {
    /**
     * who create this pm list
     */
    @JsonProperty("authorid")
    private String authorId;
    /**
     * pm list id
     */
    @JsonProperty("plid")
    private String plId;
    @JsonIgnore
    private boolean isNew;
    @JsonProperty("lastauthorid")
    private String lastAuthorid;
    @JsonProperty("lastauthor")
    private String lastAuthor;
    @JsonProperty("lastsummary")
    private String lastSummary;
    @JsonProperty("lastdateline")
    private long lastDateline;
    @JsonProperty("pmnum")
    private String pmNum;
    @JsonProperty("touid")
    private String toUid;
    @JsonProperty("tousername")
    private String toUsername;

    public PmGroup() {
    }

    @JsonCreator
    public PmGroup(@JsonProperty("isnew") String isNew) {
        this.isNew = "1".equals(isNew);
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public String getPlId() {
        return plId;
    }

    public void setPlId(String plId) {
        this.plId = plId;
    }

    public boolean getIsNew() {
        return isNew;
    }

    public void setIsNew(boolean isNew) {
        this.isNew = isNew;
    }

    public String getLastAuthorid() {
        return lastAuthorid;
    }

    public void setLastAuthorid(String lastAuthorid) {
        this.lastAuthorid = lastAuthorid;
    }

    public String getLastAuthor() {
        return lastAuthor;
    }

    public void setLastAuthor(String lastAuthor) {
        this.lastAuthor = lastAuthor;
    }

    public String getLastSummary() {
        return lastSummary;
    }

    public void setLastSummary(String lastSummary) {
        this.lastSummary = lastSummary;
    }

    public long getLastDateline() {
        return lastDateline;
    }

    public void setLastDateline(long lastDateline) {
        this.lastDateline = lastDateline;
    }

    public String getPmNum() {
        return pmNum;
    }

    public void setPmNum(String pmNum) {
        this.pmNum = pmNum;
    }

    public String getToUid() {
        return toUid;
    }

    public void setToUid(String toUid) {
        this.toUid = toUid;
    }

    public String getToUsername() {
        return toUsername;
    }

    public void setToUsername(String toUsername) {
        this.toUsername = toUsername;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PmGroup pmGroup = (PmGroup) o;
        return Objects.equal(authorId, pmGroup.authorId) &&
                Objects.equal(plId, pmGroup.plId) &&
                Objects.equal(isNew, pmGroup.isNew) &&
                Objects.equal(lastAuthorid, pmGroup.lastAuthorid) &&
                Objects.equal(lastAuthor, pmGroup.lastAuthor) &&
                Objects.equal(lastSummary, pmGroup.lastSummary) &&
                Objects.equal(lastDateline, pmGroup.lastDateline) &&
                Objects.equal(pmNum, pmGroup.pmNum) &&
                Objects.equal(toUid, pmGroup.toUid) &&
                Objects.equal(toUsername, pmGroup.toUsername);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(authorId, plId, isNew, lastAuthorid, lastAuthor, lastSummary, lastDateline, pmNum, toUid, toUsername);
    }

    @Override
    public boolean isSameItem(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PmGroup pmGroup = (PmGroup) o;
        return Objects.equal(plId, pmGroup.plId);
    }
}
