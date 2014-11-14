package cl.monsoon.s1next.model.list;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collections;
import java.util.List;

import cl.monsoon.s1next.model.Account;
import cl.monsoon.s1next.model.Post;

/**
 * {@link #postList}:
 * <pre>
 * &#x56de;&#x590d;1
 * &#x56de;&#x590d;2
 * </pre>
 */
@SuppressWarnings("UnusedDeclaration")
@JsonIgnoreProperties(ignoreUnknown = true)
public final class PostList extends Account {

    @JsonProperty("postlist")
    private List<Post> postList;

    @JsonProperty("thread")
    private Post.PostListInfo postListInfo;

    public List<Post> getPostList() {
        return Collections.unmodifiableList(postList);
    }

    public void setPostList(List<Post> postList) {
        this.postList = postList;
    }

    public Post.PostListInfo getPostListInfo() {
        return postListInfo;
    }

    public void setPostListInfo(Post.PostListInfo postListInfo) {
        this.postListInfo = postListInfo;
    }
}
