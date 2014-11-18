package cl.monsoon.s1next.model.mapper;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import cl.monsoon.s1next.model.Reply;

@SuppressWarnings("UnusedDeclaration")
@JsonIgnoreProperties(ignoreUnknown = true)
public final class ReplyWrapper implements Deserialization {

    @JsonProperty("Message")
    private Reply data;

    public Reply unwrap() {
        return data;
    }

    public void setData(Reply data) {
        this.data = data;
    }
}
