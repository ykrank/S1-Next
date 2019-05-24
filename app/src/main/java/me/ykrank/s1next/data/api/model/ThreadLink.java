package me.ykrank.s1next.data.api.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ThreadLink implements Parcelable {

    public static final Parcelable.Creator<ThreadLink> CREATOR = new Parcelable.Creator<ThreadLink>() {

        @Override
        public ThreadLink createFromParcel(Parcel source) {
            return new ThreadLink(source);
        }

        @Override
        public ThreadLink[] newArray(int size) {
            return new ThreadLink[size];
        }
    };

    private final String threadId;
    private final int jumpPage;
    private final Optional<String> quotePostId;

    private ThreadLink(Builder builder) {
        this.threadId = builder.threadId;
        this.jumpPage = builder.jumpPage;
        if (builder.quotePostId == null) {
            this.quotePostId = Optional.absent();
        } else {
            this.quotePostId = Optional.of(builder.quotePostId);
        }
    }

    private ThreadLink(Parcel source) {
        threadId = source.readString();
        jumpPage = source.readInt();
        quotePostId = Optional.fromNullable(source.readString());
    }

    /**
     * Parses thread link in order to get the meta info for this thread.
     *
     * @param url The thread link.
     * @return The {@code Optional.of(threadLink)} if we parse this thread
     * link/ID successfully, otherwise the {@code Optional.absent()}.
     */
    public static Optional<ThreadLink> parse(String url) {
        // example: http://bbs.saraba1st.com/2b/forum.php?mod=redirect&goto=findpost&pid=27217893&ptid=1074030
        Pattern pattern = Pattern.compile("ptid=(\\d+)");
        Matcher matcher = pattern.matcher(url);
        if (matcher.find()) {
            Builder builder = new Builder(matcher.group(1));

            matcher.usePattern(Pattern.compile("pid=(\\d+)"));
            if (matcher.find()) {
                builder.quotePostId(matcher.group(1));
            }

            return Optional.of(builder.build());
        }

        // example: http://bbs.saraba1st.com/2b/thread-1074030-1-1.html
        matcher.usePattern(Pattern.compile("thread-(\\d+)-(\\d+)"));
        if (matcher.find()) {
            return Optional.of(new Builder(matcher.group(1))
                    .jumpPage(Integer.parseInt(matcher.group(2)))
                    .build());
        }

        // example:
        // http://bbs.saraba1st.com/2b/forum.php?mod=viewthread&tid=1074030
        // or http://bbs.saraba1st.com/2b/archiver/tid-1074030.html
        matcher.usePattern(Pattern.compile("tid(=|-)(\\d+)"));
        if (matcher.find()) {
            Builder builder = new Builder(matcher.group(2));

            // example: http://bbs.saraba1st.com/2b/forum.php?mod=viewthread&tid=1074030&page=7
            // or http://bbs.saraba1st.com/2b/archiver/tid-1074030.html?page=7
            matcher.usePattern(Pattern.compile("page=(\\d+)"));
            if (matcher.find()) {
                builder.jumpPage(Integer.parseInt(matcher.group(1)));
            }

            return Optional.of(builder.build());
        }

        return Optional.absent();
    }

    /**
     * Parses thread link/ID in order to get the meta info for this thread.
     *
     * @param threadLinkOrId The thread link/ID.
     * @return The {@code Optional.of(threadLink)} if we parse this thread
     * link/ID successfully, otherwise the {@code Optional.absent()}.
     */
    public static Optional<ThreadLink> parse2(String threadLinkOrId) {
        // example: 1074030-1
        Pattern pattern = Pattern.compile("^(\\d+)-(\\d+)$");
        Matcher matcher = pattern.matcher(threadLinkOrId);
        if (matcher.find()) {
            Builder builder = new Builder(matcher.group(1))
                    .jumpPage(Integer.parseInt(matcher.group(2)));

            return Optional.of(builder.build());
        }

        // example: 1074030
        matcher.usePattern(Pattern.compile("^(\\d+)$"));
        if (matcher.find()) {
            Builder builder = new Builder(matcher.group(1));
            return Optional.of(builder.build());
        }

        return parse(threadLinkOrId);
    }

    public Optional<String> getQuotePostId() {
        return quotePostId;
    }

    public String getThreadId() {
        return threadId;
    }

    public int getJumpPage() {
        return jumpPage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ThreadLink that = (ThreadLink) o;
        return Objects.equal(jumpPage, that.jumpPage) &&
                Objects.equal(threadId, that.threadId) &&
                Objects.equal(quotePostId, that.quotePostId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(threadId, jumpPage, quotePostId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(threadId);
        dest.writeInt(jumpPage);
        if (quotePostId.isPresent()) {
            dest.writeString(quotePostId.get());
        } else {
            dest.writeString(null);

        }
    }

    public static final class Builder {

        private final String threadId;
        private int jumpPage = 1;
        private String quotePostId;

        public Builder(@NonNull String threadId) {
            this.threadId = Preconditions.checkNotNull(threadId);
        }

        public Builder jumpPage(int jumpPage) {
            this.jumpPage = jumpPage;
            return this;
        }

        public Builder quotePostId(String quotePostId) {
            this.quotePostId = quotePostId;
            return this;
        }

        public ThreadLink build() {
            return new ThreadLink(this);
        }
    }
}
