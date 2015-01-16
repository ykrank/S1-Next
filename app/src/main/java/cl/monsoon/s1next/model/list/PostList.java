package cl.monsoon.s1next.model.list;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

import cl.monsoon.s1next.model.Account;
import cl.monsoon.s1next.model.Post;
import cl.monsoon.s1next.util.StringHelper;

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

    @JsonProperty("thread")
    private Post.PostListInfo info;

    @JsonProperty("threadsortshow")
    private ThreadAttachment mThreadAttachment;

    @JsonProperty("postlist")
    private List<Post> data;

    public Post.PostListInfo getInfo() {
        return info;
    }

    public void setInfo(Post.PostListInfo info) {
        this.info = info;
    }

    public ThreadAttachment getThreadAttachment() {
        return mThreadAttachment;
    }

    public void setThreadAttachment(ThreadAttachment threadAttachment) {
        this.mThreadAttachment = threadAttachment;
    }

    public List<Post> getData() {
        return data;
    }

    public void setData(List<Post> data) {
        this.data = data;
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
                        StringHelper.unescapeNonBreakingSpace(value)
                                + StringUtils.defaultString(unit);
            }

            private Info(Parcel source) {
                label = source.readString();
                value = source.readString();
            }

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
            public void writeToParcel(Parcel dest, int flags) {
                dest.writeString(label);
                dest.writeString(value);
            }
        }
    }
}
