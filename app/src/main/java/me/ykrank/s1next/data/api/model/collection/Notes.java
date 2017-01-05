package me.ykrank.s1next.data.api.model.collection;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Objects;

import java.util.ArrayList;
import java.util.List;
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
    @JsonIgnore
    private List<Note> noteList;

    public Notes(@JsonProperty("list") Map<Integer, Note> list) {
        List<Note> noteList = new ArrayList<>();
        noteList.addAll(list.values());
        this.noteList = noteList;
    }

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

    public List<Note> getNoteList() {
        return noteList;
    }

    public void setNoteList(List<Note> noteList) {
        this.noteList = noteList;
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
                Objects.equal(noteList, notes.noteList);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(super.hashCode(), count, groupId, page, perPage, noteList);
    }
}
