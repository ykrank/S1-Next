package cl.monsoon.s1next.data.api.model.wrapper;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Objects;

import cl.monsoon.s1next.data.api.model.Account;
import cl.monsoon.s1next.data.api.model.Result;

@SuppressWarnings("UnusedDeclaration")
@JsonIgnoreProperties(ignoreUnknown = true)
public final class ResultWrapper {

    @JsonProperty("Message")
    private Result result;

    @JsonProperty("Variables")
    private Account account;

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ResultWrapper that = (ResultWrapper) o;
        return Objects.equal(result, that.result) &&
                Objects.equal(account, that.account);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(result, account);
    }
}
