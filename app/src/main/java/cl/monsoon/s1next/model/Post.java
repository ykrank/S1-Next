package cl.monsoon.s1next.model;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.Map;

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
        this.reply = reply;
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
     * So use {@code @JsonAnyGetter/@JsonAnySetter}
     * to handling Unknown fields.
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
