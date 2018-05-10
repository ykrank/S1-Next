package me.ykrank.s1next.data.api.model.wrapper;

import android.support.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.ykrank.androidtools.guava.Objects;

import me.ykrank.s1next.data.api.model.Account;

/**
 * Created by ykrank on 2017/2/3.
 */
@SuppressWarnings("UnusedDeclaration")
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class OriginWrapper<T extends Account> {
    @JsonProperty("Variables")
    private T data;

    @Nullable
    @JsonProperty("error")
    private String error;

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Nullable
    public String getError() {
        return error;
    }

    public void setError(@Nullable String error) {
        this.error = error;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OriginWrapper)) return false;
        OriginWrapper<?> that = (OriginWrapper<?>) o;
        return Objects.equal(data, that.data) &&
                Objects.equal(error, that.error);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(data, error);
    }

    @Override
    public String toString() {
        return "OriginWrapper{" +
                "data=" + data +
                ", error='" + error + '\'' +
                '}';
    }
}
