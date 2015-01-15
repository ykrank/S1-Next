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
        private ArrayList<Option> optionList;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public ArrayList<Option> getOptionList() {
            return optionList;
        }

        public void setOptionList(ArrayList<Option> optionList) {
            this.optionList = optionList;
        }

        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Option implements Parcelable {

            @JsonIgnore
            private final String label;

            @JsonIgnore
            private final String value;

            @SuppressWarnings("UnusedDeclaration")
            @JsonCreator
            public Option(
                    @JsonProperty("title") String label,
                    @JsonProperty("value") String value,
                    @JsonProperty("unit") String unit) {
                this.label = label;
                this.value =
                        StringHelper.unescapeNonBreakingSpace(value)
                                + StringUtils.defaultString(unit);
            }

            private Option(Parcel source) {
                label = source.readString();
                value = source.readString();
            }

            public static final Parcelable.Creator<Option> CREATOR = new Parcelable.Creator<Option>() {
                @Override
                public Option createFromParcel(Parcel source) {
                    return new Option(source);
                }

                @Override
                public Option[] newArray(int size) {
                    return new Option[size];
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
