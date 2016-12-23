package me.ykrank.s1next.data.api.model.collection;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Objects;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

import me.ykrank.s1next.data.api.model.Account;
import me.ykrank.s1next.data.api.model.Post;
import me.ykrank.s1next.data.api.model.Thread;
import me.ykrank.s1next.data.db.BlackListDbWrapper;
import me.ykrank.s1next.data.db.dbmodel.BlackList;
import me.ykrank.s1next.util.StringUtil;

@SuppressWarnings("UnusedDeclaration")
@JsonIgnoreProperties(ignoreUnknown = true)
public final class Posts extends Account {

    @JsonProperty("thread")
    private Thread postListInfo;

    @JsonProperty("threadsortshow")
    private ThreadAttachment threadAttachment;

    @JsonProperty("postlist")
    private List<Post> postList;

    public Thread getPostListInfo() {
        return postListInfo;
    }

    public void setPostListInfo(Thread postListInfo) {
        this.postListInfo = postListInfo;
    }

    public ThreadAttachment getThreadAttachment() {
        return threadAttachment;
    }

    public void setThreadAttachment(ThreadAttachment threadAttachment) {
        this.threadAttachment = threadAttachment;
    }

    public List<Post> getPostList() {
        return postList;
    }

    /**
     * @see #filterPost(Post)
     */
    public static List<Post> filterPostList(final List<Post> oPosts) {
        List<Post> posts = new ArrayList<>();
        for (Post post : oPosts) {
            Post fPost = filterPost(post);
            if (fPost != null) {
                posts.add(fPost);
            }
        }
        return posts;
    }

    /**
     * 对数据源进行处理，标记黑名单用户<br>
     * 如果修改了过滤状态，则会返回不同的对象
     */
    @Nullable
    public static Post filterPost(final Post post) {
        Post nPost = post;
        BlackListDbWrapper blackListWrapper = BlackListDbWrapper.getInstance();
        BlackList blackList = blackListWrapper.getBlackListDefault(Integer.valueOf(post.getAuthorId()), post.getAuthorName());
        if (blackList == null || blackList.post == BlackList.NORMAL) {
            if (post.isHide()) {
                nPost = post.clone();
                nPost.setHide(false);
            }
        } else if (blackList.post == BlackList.DEL_POST) {
            nPost = null;
        } else if (blackList.post == BlackList.HIDE_POST) {
            if (!post.isHide()) {
                nPost = post.clone();
                nPost.setHide(true);
            }
            nPost.setRemark(blackList.remark);
        }
        return nPost;
    }

    public void setPostList(List<Post> postList) {
        this.postList = filterPostList(postList);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Posts posts = (Posts) o;
        return Objects.equal(postListInfo, posts.postListInfo) &&
                Objects.equal(threadAttachment, posts.threadAttachment) &&
                Objects.equal(postList, posts.postList);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(super.hashCode(), postListInfo, threadAttachment, postList);
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class ThreadAttachment {

        @JsonProperty("threadsortname")
        private String title;

        @JsonProperty("optionlist")
        private ArrayList<Info> infoList;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public ArrayList<Info> getInfoList() {
            return infoList;
        }

        public void setInfoList(ArrayList<Info> infoList) {
            this.infoList = infoList;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ThreadAttachment that = (ThreadAttachment) o;
            return Objects.equal(title, that.title) &&
                    Objects.equal(infoList, that.infoList);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(title, infoList);
        }

        @JsonIgnoreProperties(ignoreUnknown = true)
        public static final class Info implements Parcelable {

            public static final Parcelable.Creator<Info> CREATOR = new Parcelable.Creator<Info>() {

                @Override
                public Info createFromParcel(Parcel source) {
                    return new Info(source);
                }

                @Override
                public Info[] newArray(int size) {
                    return new Info[size];
                }
            };

            @JsonIgnore
            private final String label;

            @JsonIgnore
            private final String value;

            @JsonCreator
            @SuppressWarnings("UnusedDeclaration")
            public Info(@JsonProperty("title") String label,
                        @JsonProperty("value") String value,
                        @JsonProperty("unit") String unit) {
                this.label = label;
                this.value = StringUtil.unescapeNonBreakingSpace(value)
                        + StringUtils.defaultString(unit);
            }

            private Info(Parcel source) {
                label = source.readString();
                value = source.readString();
            }

            public String getLabel() {
                return label;
            }

            public String getValue() {
                return value;
            }

            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;
                Info info = (Info) o;
                return Objects.equal(label, info.label) &&
                        Objects.equal(value, info.value);
            }

            @Override
            public int hashCode() {
                return Objects.hashCode(label, value);
            }

            @Override
            public int describeContents() {
                return 0;
            }

            @Override
            public void writeToParcel(Parcel dest, int flags) {
                dest.writeString(label);
                dest.writeString(value);
            }
        }
    }
}
