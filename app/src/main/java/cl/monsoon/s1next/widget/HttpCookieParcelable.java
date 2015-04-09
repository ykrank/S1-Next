package cl.monsoon.s1next.widget;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.net.HttpCookie;

public final class HttpCookieParcelable implements Parcelable {

    public static final Creator<HttpCookieParcelable> CREATOR =
            new Creator<HttpCookieParcelable>() {

                @Override
                public HttpCookieParcelable createFromParcel(@NonNull Parcel source) {
                    return new HttpCookieParcelable(source);
                }

                @Override
                @NonNull
                public HttpCookieParcelable[] newArray(int i) {
                    return new HttpCookieParcelable[i];
                }
            };

    private final HttpCookie mHttpCookie;

    public HttpCookieParcelable(HttpCookie httpCookie) {
        this.mHttpCookie = httpCookie;
    }

    private HttpCookieParcelable(Parcel source) {
        String name = source.readString();
        String value = source.readString();
        mHttpCookie = new HttpCookie(name, value);
        mHttpCookie.setComment(source.readString());
        mHttpCookie.setCommentURL(source.readString());
        mHttpCookie.setDiscard(source.readByte() != 0);
        mHttpCookie.setDomain(source.readString());
        mHttpCookie.setMaxAge(source.readLong());
        mHttpCookie.setPath(source.readString());
        mHttpCookie.setPortlist(source.readString());
        mHttpCookie.setSecure(source.readByte() != 0);
        mHttpCookie.setVersion(source.readInt());
    }

    public HttpCookie getHttpCookie() {
        return mHttpCookie;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(mHttpCookie.getName());
        dest.writeString(mHttpCookie.getValue());
        dest.writeString(mHttpCookie.getComment());
        dest.writeString(mHttpCookie.getCommentURL());
        dest.writeByte((byte) (mHttpCookie.getDiscard() ? 1 : 0));
        dest.writeString(mHttpCookie.getDomain());
        dest.writeLong(mHttpCookie.getMaxAge());
        dest.writeString(mHttpCookie.getPath());
        dest.writeString(mHttpCookie.getPortlist());
        dest.writeByte((byte) (mHttpCookie.getSecure() ? 1 : 0));
        dest.writeInt(mHttpCookie.getVersion());
    }
}
