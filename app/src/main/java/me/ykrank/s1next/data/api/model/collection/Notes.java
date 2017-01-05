package me.ykrank.s1next.data.api.model.collection;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Objects;

import java.util.Map;

import me.ykrank.s1next.data.api.model.Account;
import me.ykrank.s1next.data.api.model.Note;

/**
 * Created by ykrank on 2017/1/5.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Notes extends Account {
    @JsonProperty("count")
    private int count;
    @JsonProperty("groupid")
    private int groupId;
    @JsonProperty("page")
    private int page;
    @JsonProperty("perpage")
    private int perPage;
    @JsonProperty("list")
    private Map<Integer, Note> datas;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPerPage() {
        return perPage;
    }

    public void setPerPage(int perPage) {
        this.perPage = perPage;
    }

    public Map<Integer, Note> getDatas() {
        return datas;
    }

    public void setDatas(Map<Integer, Note> datas) {
        this.datas = datas;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Notes)) return false;
        if (!super.equals(o)) return false;
        Notes notes = (Notes) o;
        return count == notes.count &&
                groupId == notes.groupId &&
                page == notes.page &&
                perPage == notes.perPage &&
                Objects.equal(datas, notes.datas);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(super.hashCode(), count, groupId, page, perPage, datas);
    }
}
