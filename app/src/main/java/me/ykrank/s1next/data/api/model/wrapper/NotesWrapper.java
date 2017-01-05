package me.ykrank.s1next.data.api.model.wrapper;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Objects;

import me.ykrank.s1next.data.api.model.collection.Notes;

/**
 * Created by ykrank on 2017/1/5.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class NotesWrapper {
    @JsonProperty("Variables")
    private Notes notes;

    public Notes getNotes() {
        return notes;
    }

    public void setNotes(Notes notes) {
        this.notes = notes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NotesWrapper)) return false;
        NotesWrapper that = (NotesWrapper) o;
        return Objects.equal(notes, that.notes);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(notes);
    }
}
