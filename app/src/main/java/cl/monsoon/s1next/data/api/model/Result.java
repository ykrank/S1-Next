package cl.monsoon.s1next.data.api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import cl.monsoon.s1next.data.api.DiscuzMessageFormatter;

@SuppressWarnings("UnusedDeclaration")
@JsonIgnoreProperties(ignoreUnknown = true)
public final class Result {

    @JsonProperty("messageval")
    private String status;

    @JsonProperty("messagestr")
    private String message;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = DiscuzMessageFormatter.addFullStopIfNeeded(message);
    }
}
