package me.ykrank.s1next.data.api.model.wrapper;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Objects;

import me.ykrank.s1next.data.api.model.Account;
import me.ykrank.s1next.data.api.model.Result;

@SuppressWarnings("UnusedDeclaration")
@JsonIgnoreProperties(ignoreUnknown = true)
public class BaseResultWrapper<T extends Account> extends OriginWrapper<T> {
    
    @JsonProperty("Message")
    private Result result;

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BaseResultWrapper)) return false;
        if (!super.equals(o)) return false;
        BaseResultWrapper<?> that = (BaseResultWrapper<?>) o;
        return Objects.equal(result, that.result);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(super.hashCode(), result);
    }

    @Override
    public String toString() {
        return "BaseResultWrapper{" +
                "result=" + result +
                '}';
    }
}
