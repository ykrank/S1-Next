package me.ykrank.s1next.data.api.model.wrapper;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Objects;

import me.ykrank.s1next.data.api.model.collection.Threads;

@SuppressWarnings("UnusedDeclaration")
@JsonIgnoreProperties(ignoreUnknown = true)
public final class PmWrapper {

    @JsonProperty("Variables")
    private Threads threads;

    public Threads getThreads() {
        return threads;
    }

    public void setThreads(Threads threads) {
        this.threads = threads;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PmWrapper that = (PmWrapper) o;
        return Objects.equal(threads, that.threads);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(threads);
    }
}
