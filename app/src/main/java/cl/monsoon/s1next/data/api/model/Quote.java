package cl.monsoon.s1next.data.api.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import org.apache.commons.lang3.StringEscapeUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cl.monsoon.s1next.util.ServerException;

public final class Quote implements Extractable, Parcelable {

    public static final Parcelable.Creator<Quote> CREATOR =
            new Parcelable.Creator<Quote>() {
                @Override
                public Quote createFromParcel(Parcel source) {
                    return new Quote(source);
                }

                @Override
                public Quote[] newArray(int size) {
                    return new Quote[size];
                }
            };

    /**
     * The quoted user identification which was encoded in server.
     * Without this we can't notify the user.
     */
    private String encodedUserId;

    /**
     * The processed quoted content which has some HTML tags and
     * its origin redirect hyperlink.
     */
    private String quoteMessage;

    private Quote() {

    }

    private Quote(Parcel source) {
        encodedUserId = source.readString();
        quoteMessage = source.readString();
    }

    public String getEncodedUserId() {
        return encodedUserId;
    }

    private void setEncodedUserId(String encodedUserId) {
        this.encodedUserId = encodedUserId;
    }

    public String getQuoteMessage() {
        return quoteMessage;
    }

    private void setQuoteMessage(String quoteMessage) {
        this.quoteMessage = quoteMessage;
    }

    /**
     * Extracts XML string into POJO.
     *
     * @throws cl.monsoon.s1next.util.ServerException if XML parsing error occurs
     */
    public static Quote fromXmlString(String xmlString) throws ServerException {
        // example: <input type="hidden" name="noticeauthor" value="d755gUR1jP9eeoTPkiOyz3FxvLzpFLJsSFvJA8uAfBg" />
        Pattern pattern = Pattern.compile("name=\"noticeauthor\"\\svalue=\"(\\p{ASCII}+)\"\\s/>");
        Matcher matcher = pattern.matcher(xmlString);

        Quote quote = new Quote();
        if (matcher.find()) {
            quote.setEncodedUserId(matcher.group(1));

            // example: <input type="hidden" name="noticetrimstr" value="[quote][size=2][url=forum.php?mod=redirect&amp;goto=findpost&amp;pid=1&amp;ptid=1][color=#999999]VVV 发表于 2014-12-13 10:11[/color][/url][/size]
            pattern = Pattern.compile("name=\"noticetrimstr\"\\svalue=\"(.+?)\"\\s/>",
                    Pattern.DOTALL);
            matcher.usePattern(pattern);
            if (matcher.find()) {
                // unescape ampersand (&amp;)
                quote.setQuoteMessage(StringEscapeUtils.unescapeXml(matcher.group(1)));
            }
        }

        if (TextUtils.isEmpty(quote.getEncodedUserId())
                || TextUtils.isEmpty(quote.getQuoteMessage())) {
            throw new ServerException(quote + "'s each field can't be empty.");
        }

        return quote;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(encodedUserId);
        dest.writeString(quoteMessage);
    }
}
