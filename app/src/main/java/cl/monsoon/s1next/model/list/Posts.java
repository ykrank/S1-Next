package cl.monsoon.s1next.model.list;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

import cl.monsoon.s1next.model.Account;
import cl.monsoon.s1next.model.Post;
import cl.monsoon.s1next.util.StringUtil;

/**
 * {@link #postList}:
 * <pre>
 * 回复1
 * 回复2
 * </pre>
 */
@SuppressWarnings("UnusedDeclaration")
@JsonIgnoreProperties(ignoreUnknown = true)
public final class Posts extends Account {

    @JsonProperty("thread")
    private cl.monsoon.s1next.model.Thread postListInfo;

    @JsonProperty("threadsortshow")
    private ThreadAttachment threadAttachment;

    @JsonProperty("postlist")
    private List<Post> postList;

    public cl.monsoon.s1next.model.Thread getPostListInfo() {
        return postListInfo;
    }

    public void setPostListInfo(cl.monsoon.s1next.model.Thread postListInfo) {
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

    public void setPostList(List<Post> postList) {
        this.postList = postList;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ThreadAttachment {

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

        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Info implements Parcelable {

            @JsonIgnore
            private final String label;

            @JsonIgnore
            private final String value;

            @SuppressWarnings("UnusedDeclaration")
            @JsonCreator
            public Info(
                    @JsonProperty("title") String label,
                    @JsonProperty("value") String value,
                    @JsonProperty("unit") String unit) {
                this.label = label;
                this.value =
                        StringUtil.unescapeNonBreakingSpace(value)
                                + StringUtils.defaultString(unit);
            }

            private Info(Parcel source) {
                label = source.readString();
                value = source.readString();
            }

            public static final Parcelable.Creator<Info> CREATOR =
                    new Parcelable.Creator<Info>() {
                        @Override
                        public Info createFromParcel(@NonNull Parcel source) {
                            return new Info(source);
                        }

                        @Override
                        @NonNull
                        public Info[] newArray(int size) {
                            return new Info[size];
                        }
                    };

            public String getLabel() {
                return label;
            }

            public String getValue() {
                return value;
            }

            @Override
            public int describeContents() {
                return 0;
            }

            @Override
            public void writeToParcel(@NonNull Parcel dest, int flags) {
                dest.writeString(label);
                dest.writeString(value);
            }
        }
    }
}
