package me.ykrank.s1next.data.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.ykrank.androidtools.guava.Objects;
import com.github.ykrank.androidtools.ui.adapter.model.SameItem;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Pm implements Cloneable, SameItem {
    @JsonProperty("plid")
    private String plId;
    @JsonProperty("pmid")
    private String pmId;
    @JsonProperty("pmtype")
    private String pmType;
    @JsonProperty("authorid")
    private String authorId;
    private String author;
    private String subject;
    private String message;
    private long dateline;
    @JsonProperty("msgfromid")
    private String msgFromId;
    @JsonProperty("msgfrom")
    private String msgFrom;
    @JsonProperty("msgtoid")
    private String msgToId;
    //no this data in api, should set it manual
    @JsonIgnore
    private String msgTo;

    public String getPlId() {
        return plId;
    }

    public void setPlId(String plId) {
        this.plId = plId;
    }

    public String getPmId() {
        return pmId;
    }

    public void setPmId(String pmId) {
        this.pmId = pmId;
    }

    public String getPmType() {
        return pmType;
    }

    public void setPmType(String pmType) {
        this.pmType = pmType;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getDateline() {
        return dateline;
    }

    public void setDateline(long dateline) {
        this.dateline = dateline;
    }

    public String getMsgFromId() {
        return msgFromId;
    }

    public void setMsgFromId(String msgFromId) {
        this.msgFromId = msgFromId;
    }

    public String getMsgFrom() {
        return msgFrom;
    }

    public void setMsgFrom(String msgFrom) {
        this.msgFrom = msgFrom;
    }

    public String getMsgToId() {
        return msgToId;
    }

    public void setMsgToId(String msgToId) {
        this.msgToId = msgToId;
    }

    public String getMsgTo() {
        return msgTo;
    }

    public void setMsgTo(String msgTo) {
        this.msgTo = msgTo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pm pm = (Pm) o;
        return Objects.equal(plId, pm.plId) &&
                Objects.equal(pmId, pm.pmId) &&
                Objects.equal(pmType, pm.pmType) &&
                Objects.equal(authorId, pm.authorId) &&
                Objects.equal(author, pm.author) &&
                Objects.equal(subject, pm.subject) &&
                Objects.equal(message, pm.message) &&
                Objects.equal(dateline, pm.dateline) &&
                Objects.equal(msgFromId, pm.msgFromId) &&
                Objects.equal(msgFrom, pm.msgFrom) &&
                Objects.equal(msgToId, pm.msgToId) &&
                Objects.equal(msgTo, pm.msgTo);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(plId, pmId, pmType, authorId, author, subject, message, dateline, msgFromId, msgFrom, msgToId, msgTo);
    }

    @Override
    public boolean isSameItem(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pm pm = (Pm) o;
        return Objects.equal(plId, pm.plId) &&
                Objects.equal(pmId, pm.pmId);
    }
}
