package me.ykrank.s1next.data.api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.ykrank.androidtools.guava.Objects;

import me.ykrank.s1next.data.api.DiscuzMessageFormatter;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Result result = (Result) o;
        return Objects.equal(status, result.status) &&
                Objects.equal(message, result.message);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(status, message);
    }

    @Override
    public String toString() {
        return "Result{" +
                "status='" + status + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
