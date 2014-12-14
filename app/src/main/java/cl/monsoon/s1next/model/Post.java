package cl.monsoon.s1next.model;

import android.graphics.Color;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("UnusedDeclaration")
@JsonIgnoreProperties(ignoreUnknown = true)
public final class Post {

    @JsonProperty("author")
    private String username;

    @JsonProperty("authorid")
    private String userId;

    @JsonProperty("message")
    private String reply;

    @JsonProperty("number")
    private String count;

    @JsonProperty("dbdateline")
    private long time;

    @JsonProperty("attachments")
    private AttachmentWrapper attachmentWrapper;

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

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        // Map colors (see mapColors(String))
        // and replace "imgwidth" with "img width",
        // because some img tags in S1 aren't correct.
        // This may be the best way to deal with it though
        // we may replace something wrong by accident.
        this.reply = mapColors(reply).replaceAll("<imgwidth=\"", "<img width=\"");
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public long getTime() {
        return time;
    }

    /**
     * Convert seconds to milliseconds.
     */
    public void setTime(long time) {
        this.time = time * 1000;
    }

    public AttachmentWrapper getAttachmentWrapper() {
        return attachmentWrapper;
    }

    public void setAttachmentWrapper(AttachmentWrapper attachmentWrapper) {
        this.attachmentWrapper = attachmentWrapper;
    }

    /**
     * {@link Color} doesn't support all HTML color names.
     * So {@link android.text.Html#fromHtml(String)} won't
     * map some color names for replies in S1.
     * We need to map these color name to its hex value by ourselves.
     */
    private static String mapColors(CharSequence reply) {
        // sample: color="sienna"
        // matcher.group(0): color="sienna"
        // matcher.group(1): sienna
        Pattern pattern = Pattern.compile("color=\"([a-zA-Z]+)\"");
        Matcher matcher = pattern.matcher(reply);

        StringBuffer stringBuffer = new StringBuffer();
        String color;
        while (matcher.find()) {
            // get color hex value for its color name
            color = sColorNameMap.get(matcher.group(1).toLowerCase());
            if (color == null) {
                // throw new IllegalStateException("sColorNameMap" + "must contain " + matcher.group(0));
                continue;
            }
            // append part of the string and plus its color hex value
            matcher.appendReplacement(stringBuffer, "color=\"" + color + "\"");
        }
        matcher.appendTail(stringBuffer);

        return stringBuffer.toString();
    }

    private static final Map<String, String> sColorNameMap;

    static {
        sColorNameMap = new HashMap<>();

        sColorNameMap.put("sienna", "#A0522D");
        sColorNameMap.put("darkolivegreen", "#556B2F");
        sColorNameMap.put("darkgreen", "#006400");
        sColorNameMap.put("darkslateblue", "#483D8B");
        sColorNameMap.put("indigo", "#4B0082");
        sColorNameMap.put("darkslategray", "#2F4F4F");
        sColorNameMap.put("darkred", "#8B0000");
        sColorNameMap.put("darkorange", "#FF8C00");
        sColorNameMap.put("slategray", "#708090");
        sColorNameMap.put("dimgray", "#696969");
        sColorNameMap.put("sandybrown", "#F4A460");
        sColorNameMap.put("yellowgreen", "#9ACD32");
        sColorNameMap.put("seagreen", "#2E8B57");
        sColorNameMap.put("mediumturquoise", "#48D1CC");
        sColorNameMap.put("royalblue", "#4169E1");
        sColorNameMap.put("orange", "#FFA500");
        sColorNameMap.put("deepskyblue", "#00BFFF");
        sColorNameMap.put("darkorchid", "#9932CC");
        sColorNameMap.put("pink", "#FFC0CB");
        sColorNameMap.put("wheat", "#F5DEB3");
        sColorNameMap.put("lemonchiffon", "#FFFACD");
        sColorNameMap.put("palegreen", "#98FB98");
        sColorNameMap.put("paleturquoise", "#AFEEEE");
        sColorNameMap.put("lightblue", "#ADD8E6");
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PostListInfo {

        @JsonProperty("replies")
        private int replies;

        public int getReplies() {
            return replies;
        }

        public void setReplies(int replies) {
            this.replies = replies;
        }
    }

    /**
     * Example:
     * <pre>
     * {
     *   "reply": "[attach]13[\/attach]",
     *   "attachments": {
     *     "13": {
     *       "attachment": "201410\/1\/1.png",
     *        "url": "http:\/\/img.saraba1st.com\/attachments\/forum\/",
     *      }
     *   }
     * }
     * </pre>
     * <p>
     * So use {@code @JsonAnyGetter/@JsonAnySetter} to handle Unknown fields.
     */
    public static class AttachmentWrapper {

        @JsonIgnore
        private final Map<String, Attachment> attachmentMap = new HashMap<>();

        public Map<String, Attachment> getAttachmentMap() {
            return attachmentMap;
        }

        @JsonAnySetter
        public void setAttachment(String name, Attachment value) {
            attachmentMap.put(name, value);
        }

        public boolean hasAttachments() {
            return !attachmentMap.isEmpty();
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Attachment {

        @JsonIgnore
        private final String url;

        @JsonCreator
        public Attachment(
                @JsonProperty("url") String urlPrefix,
                @JsonProperty("attachment") String urlSuffix
        ) {
            this.url = urlPrefix + urlSuffix;
        }

        public String getUrl() {
            return url;
        }
    }
}
