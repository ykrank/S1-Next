package cl.monsoon.s1next.widget;

import android.os.Parcel;
import android.os.Parcelable;

import java.net.HttpCookie;

public final class HttpCookieParcelable implements Parcelable {

    public static final Creator<HttpCookieParcelable> CREATOR =
            new Creator<HttpCookieParcelable>() {

                @Override
                public HttpCookieParcelable createFromParcel(Parcel parcel) {
                    return new HttpCookieParcelable(parcel);
                }

                @Override
                public HttpCookieParcelable[] newArray(int i) {
                    return new HttpCookieParcelable[i];
                }
            };

    private final HttpCookie mHttpCookie;

    public HttpCookieParcelable(HttpCookie httpCookie) {
        this.mHttpCookie = httpCookie;
    }

    private HttpCookieParcelable(Parcel parcel) {
        String name = parcel.readString();
        String value = parcel.readString();
        mHttpCookie = new HttpCookie(name, value);
        mHttpCookie.setComment(parcel.readString());
        mHttpCookie.setCommentURL(parcel.readString());
        mHttpCookie.setDiscard(parcel.readByte() != 0);
        mHttpCookie.setDomain(parcel.readString());
        mHttpCookie.setMaxAge(parcel.readLong());
        mHttpCookie.setPath(parcel.readString());
        mHttpCookie.setPortlist(parcel.readString());
        mHttpCookie.setSecure(parcel.readByte() != 0);
        mHttpCookie.setVersion(parcel.readInt());
    }

    public HttpCookie getHttpCookie() {
        return mHttpCookie;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
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
