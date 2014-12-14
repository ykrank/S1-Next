package cl.monsoon.s1next.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @see cl.monsoon.s1next.Api#URL_QUOTE_HELPER
 */
public final class Quote implements Extractable, Parcelable {

    // noticeauthor which was encoded
    private String encodedUserId;
    // noticetrimstr
    private String quoteMessage;

    public Quote() {

    }

    Quote(Parcel source) {
        encodedUserId = source.readString();
        quoteMessage = source.readString();
    }

    public static final Parcelable.Creator<Quote> CREATOR = new Parcelable.Creator<Quote>() {
        @Override
        public Quote createFromParcel(Parcel source) {
            return new Quote(source);
        }

        @Override
        public Quote[] newArray(int size) {
            return new Quote[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(encodedUserId);
        dest.writeString(quoteMessage);
    }

    public String getEncodedUserId() {
        return encodedUserId;
    }

    public void setEncodedUserId(String encodedUserId) {
        this.encodedUserId = encodedUserId;
    }

    public String getQuoteMessage() {
        return quoteMessage;
    }

    public void setQuoteMessage(String quoteMessage) {
        this.quoteMessage = quoteMessage;
    }

    public static Quote fromXmlString(String xmlString) throws IOException {
        // sample: <input type="hidden" name="noticeauthor" value="d755gUR1jP9eeoTPkiOyz3FxvLzpFLJsSFvJA8uAfBg" />
        Pattern pattern =
                Pattern.compile("name=\"noticeauthor\"\\svalue=\"(\\p{ASCII}+)\"\\s/>");

        Matcher matcher = pattern.matcher(xmlString);

        Quote quote = new Quote();
        if (matcher.find()) {
            quote.setEncodedUserId(matcher.group(1));

            // sample: <input type="hidden" name="noticetrimstr" value="[quote][size=2][url=forum.php?mod=redirect&amp;goto=findpost&amp;pid=1&amp;ptid=1][color=#999999]VVV 发表于 2014-12-13 10:11[/color][/url][/size]
            pattern = Pattern.compile(
                    "name=\"noticetrimstr\"\\svalue=\"(.+?)\"\\s/>", Pattern.DOTALL);
            matcher.usePattern(pattern);
            if (matcher.find()) {
                quote.setQuoteMessage(matcher.group(1));
            }
        }

        if (TextUtils.isEmpty(quote.getEncodedUserId())
                || TextUtils.isEmpty(quote.getQuoteMessage())) {
            throw new IOException(quote + "'s each field can't be empty.");
        }

        return quote;
    }
}
