package cl.monsoon.s1next.data.api.model;

import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v4.util.SimpleArrayMap;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Objects;

import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("UnusedDeclaration")
@JsonIgnoreProperties(ignoreUnknown = true)
public final class Post {

    @JsonProperty("pid")
    private String id;

    @JsonProperty("author")
    private String username;

    @JsonProperty("authorid")
    private String userId;

    @JsonProperty("message")
    private String reply;

    @JsonProperty("number")
    private String count;

    @JsonProperty("dbdateline")
    private long datetime;

    @JsonProperty("attachments")
    private Map<Integer, Attachment> attachmentMap;

    public Post() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * Replies are null sometimes.
     * <p>
     * See https://github.com/floating-cat/S1-Next/issues/6
     */
    @Nullable
    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        // Replaces "imgwidth" with "img width",
        // because some img tags in S1 aren't correct.
        // This may be the best way to deal with it though
        // we may replace something wrong by accident.
        // Also maps some colors, see mapColors(String).
        this.reply = mapColors(reply).replaceAll("<imgwidth=\"", "<img width=\"");

        processAttachment();
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public long getDatetime() {
        return datetime;
    }

    public void setDatetime(long datetime) {
        // convert seconds to milliseconds
        this.datetime = TimeUnit.SECONDS.toMillis(datetime);
    }

    public void setAttachmentMap(Map<Integer, Attachment> attachmentMap) {
        this.attachmentMap = attachmentMap;

        processAttachment();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Post post = (Post) o;
        return Objects.equal(datetime, post.datetime) &&
                Objects.equal(id, post.id) &&
                Objects.equal(username, post.username) &&
                Objects.equal(userId, post.userId) &&
                Objects.equal(reply, post.reply) &&
                Objects.equal(count, post.count) &&
                Objects.equal(attachmentMap, post.attachmentMap);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id, username, userId, reply, count, datetime, attachmentMap);
    }

    /**
     * {@link Color} doesn't support all HTML color names.
     * So {@link android.text.Html#fromHtml(String)} won't
     * map some color names for replies in S1.
     * We need to map these color names to their hex value.
     */
    private static String mapColors(String reply) {
        // example: color="sienna"
        // matcher.group(0): color="sienna"
        // matcher.group(1): sienna
        Matcher matcher = Pattern.compile("color=\"([a-zA-Z]+)\"").matcher(reply);

        StringBuffer stringBuffer = new StringBuffer();
        String color;
        while (matcher.find()) {
            // get color hex value for its color name
            color = COLOR_NAME_MAP.get(matcher.group(1).toLowerCase(Locale.US));
            if (color == null) {
                continue;
            }
            // append part of the string and its color hex value
            matcher.appendReplacement(stringBuffer, "color=\"" + color + "\"");
        }
        matcher.appendTail(stringBuffer);

        return stringBuffer.toString();
    }

    /**
     * Replaces attach tags with HTML img tags
     * in order to display attachment images in TextView.
     * <p>
     * Also concats the missing img tag from attachment.
     * See https://github.com/floating-cat/S1-Next/issues/7
     */
    private void processAttachment() {
        if (reply == null || attachmentMap == null) {
            return;
        }

        for (Map.Entry<Integer, Post.Attachment> entry : attachmentMap.entrySet()) {
            Post.Attachment attachment = entry.getValue();
            String imgTag = "<img src=\"" + attachment.getUrl() + "\" />";
            String replyCopy = reply;
            // get the original string if there is nothing to replace
            reply = reply.replace("[attach]" + entry.getKey() + "[/attach]", imgTag);
            //noinspection StringEquality
            if (reply == replyCopy) {
                // concat the missing img tag
                reply = reply + imgTag;
            }
        }
    }

    private static final SimpleArrayMap<String, String> COLOR_NAME_MAP;

    static {
        COLOR_NAME_MAP = new SimpleArrayMap<>();

        COLOR_NAME_MAP.put("sienna", "#A0522D");
        COLOR_NAME_MAP.put("darkolivegreen", "#556B2F");
        COLOR_NAME_MAP.put("darkgreen", "#006400");
        COLOR_NAME_MAP.put("darkslateblue", "#483D8B");
        COLOR_NAME_MAP.put("indigo", "#4B0082");
        COLOR_NAME_MAP.put("darkslategray", "#2F4F4F");
        COLOR_NAME_MAP.put("darkred", "#8B0000");
        COLOR_NAME_MAP.put("darkorange", "#FF8C00");
        COLOR_NAME_MAP.put("slategray", "#708090");
        COLOR_NAME_MAP.put("dimgray", "#696969");
        COLOR_NAME_MAP.put("sandybrown", "#F4A460");
        COLOR_NAME_MAP.put("yellowgreen", "#9ACD32");
        COLOR_NAME_MAP.put("seagreen", "#2E8B57");
        COLOR_NAME_MAP.put("mediumturquoise", "#48D1CC");
        COLOR_NAME_MAP.put("royalblue", "#4169E1");
        COLOR_NAME_MAP.put("orange", "#FFA500");
        COLOR_NAME_MAP.put("deepskyblue", "#00BFFF");
        COLOR_NAME_MAP.put("darkorchid", "#9932CC");
        COLOR_NAME_MAP.put("pink", "#FFC0CB");
        COLOR_NAME_MAP.put("wheat", "#F5DEB3");
        COLOR_NAME_MAP.put("lemonchiffon", "#FFFACD");
        COLOR_NAME_MAP.put("palegreen", "#98FB98");
        COLOR_NAME_MAP.put("paleturquoise", "#AFEEEE");
        COLOR_NAME_MAP.put("lightblue", "#ADD8E6");

        // https://code.google.com/p/android/issues/detail?id=75953
        COLOR_NAME_MAP.put("white", "#FFFFFF");
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Attachment {

        @JsonIgnore
        private final String url;

        @JsonCreator
        public Attachment(@JsonProperty("url") String urlPrefix,
                          @JsonProperty("attachment") String urlSuffix) {
            this.url = urlPrefix + urlSuffix;
        }

        public String getUrl() {
            return url;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Attachment that = (Attachment) o;
            return Objects.equal(url, that.url);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(url);
        }
    }
}
