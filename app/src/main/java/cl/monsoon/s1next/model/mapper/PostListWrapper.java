package cl.monsoon.s1next.model.mapper;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import cl.monsoon.s1next.model.Result;
import cl.monsoon.s1next.model.list.PostList;

@SuppressWarnings("UnusedDeclaration")
@JsonIgnoreProperties(ignoreUnknown = true)
public final class PostListWrapper implements Deserializable {

    @JsonProperty("Variables")
    private PostList data;

    @JsonProperty("Message")
    private Result result;

    public PostList unwrap() {
        return data;
    }

    @SuppressWarnings("UnusedDeclaration")
    public void setData(PostList data) {
        this.data = data;
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }
}
