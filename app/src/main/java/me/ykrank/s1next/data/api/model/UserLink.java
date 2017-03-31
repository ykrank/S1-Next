package me.ykrank.s1next.data.api.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.common.base.Objects;
import com.google.common.base.Optional;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class UserLink implements Parcelable {
    private String uid;

    public UserLink(String uid) {
        this.uid = uid;
    }

    protected UserLink(Parcel in) {
        uid = in.readString();
    }

    public static final Creator<UserLink> CREATOR = new Creator<UserLink>() {
        @Override
        public UserLink createFromParcel(Parcel in) {
            return new UserLink(in);
        }

        @Override
        public UserLink[] newArray(int size) {
            return new UserLink[size];
        }
    };

    /**
     * Parses user link in order to get the meta info for this user.
     *
     * @param url The user space link.
     * @return The {@code Optional.of(userLink)} if we parse this user
     * link/ID successfully, otherwise the {@code Optional.absent()}.
     */
    public static Optional<UserLink> parse(String url) {
        // example: space-uid-223963.html
        Pattern pattern = Pattern.compile("space-uid-(\\d+)");
        Matcher matcher = pattern.matcher(url);
        if (matcher.find()) {
            return Optional.of(new UserLink(matcher.group(1)));
        }
        return Optional.absent();
    }

    /**
     * Parses user link/ID in order to get the meta info for this user.
     *
     * @param spaceLinkOrId The user space link/ID.
     * @return The {@code Optional.of(userLink)} if we parse this user
     * link/ID successfully, otherwise the {@code Optional.absent()}.
     */
    public static Optional<UserLink> parse2(String spaceLinkOrId) {
        // example: 223963
        Pattern pattern = Pattern.compile("^(\\d+)$");
        Matcher matcher = pattern.matcher(spaceLinkOrId);
        if (matcher.find()) {
            return Optional.of(new UserLink(matcher.group(1)));
        }
        return Optional.absent();
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserLink userLink = (UserLink) o;
        return Objects.equal(uid, userLink.uid);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(uid);
    }

    @Override
    public String toString() {
        return "UserLink{" +
                "uid='" + uid + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(uid);
    }
}
