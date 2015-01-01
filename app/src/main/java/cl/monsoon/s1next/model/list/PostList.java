package cl.monsoon.s1next.model.list;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import cl.monsoon.s1next.model.Account;
import cl.monsoon.s1next.model.Post;

/**
 * {@link #data}:
 * <pre>
 * 回复1
 * 回复2
 * </pre>
 */
@SuppressWarnings("UnusedDeclaration")
@JsonIgnoreProperties(ignoreUnknown = true)
public final class PostList extends Account {

    @JsonProperty("postlist")
    private List<Post> data;

    @JsonProperty("thread")
    private Post.PostListInfo info;

    public List<Post> getData() {
        return data;
    }

    public void setData(List<Post> data) {
        this.data = data;
    }

    public Post.PostListInfo getInfo() {
        return info;
    }

    public void setInfo(Post.PostListInfo info) {
        this.info = info;
    }
}
