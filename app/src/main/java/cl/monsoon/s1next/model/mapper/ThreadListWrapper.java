package cl.monsoon.s1next.model.mapper;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import cl.monsoon.s1next.model.list.ThreadList;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class ThreadListWrapper implements Deserialization {

    @JsonProperty("Variables")
    private ThreadList data;

    public ThreadList unwrap() {
        return data;
    }

    @SuppressWarnings("UnusedDeclaration")
    public void setData(ThreadList data) {
        this.data = data;
    }
}
