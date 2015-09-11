package cl.monsoon.s1next.data.api.model;

import android.text.TextUtils;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

import org.apache.commons.lang3.StringEscapeUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Quote {

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

    private Quote() {}

    public String getEncodedUserId() {
        return encodedUserId;
    }

    public String getQuoteMessage() {
        return quoteMessage;
    }

    /**
     * Extracts {@link Quote} from XML string.
     */
    public static Quote fromXmlString(String xmlString) {
        // example: <input type="hidden" name="noticeauthor" value="d755gUR1jP9eeoTPkiOyz3FxvLzpFLJsSFvJA8uAfBg" />
        Pattern pattern = Pattern.compile("name=\"noticeauthor\"\\svalue=\"(\\p{ASCII}+)\"\\s/>");
        Matcher matcher = pattern.matcher(xmlString);

        Quote quote = new Quote();
        if (matcher.find()) {
            quote.encodedUserId = matcher.group(1);

            // example: <input type="hidden" name="noticetrimstr" value="[quote][size=2][url=forum.php?mod=redirect&amp;goto=findpost&amp;pid=1&amp;ptid=1][color=#999999]VVV 发表于 2014-12-13 10:11[/color][/url][/size]
            pattern = Pattern.compile("name=\"noticetrimstr\"\\svalue=\"(.+?)\"\\s/>",
                    Pattern.DOTALL);
            matcher.usePattern(pattern);
            if (matcher.find()) {
                // unescape ampersand (&amp;)
                quote.quoteMessage = StringEscapeUtils.unescapeXml(matcher.group(1));
            }
        }

        Preconditions.checkState(!TextUtils.isEmpty(quote.getEncodedUserId())
                && !TextUtils.isEmpty(quote.getQuoteMessage()), "Cannot get the quote information.");

        return quote;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Quote quote = (Quote) o;
        return Objects.equal(encodedUserId, quote.encodedUserId) &&
                Objects.equal(quoteMessage, quote.quoteMessage);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(encodedUserId, quoteMessage);
    }
}
