package cl.monsoon.s1next.model.mapper;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import cl.monsoon.s1next.model.Account;
import cl.monsoon.s1next.model.Result;

@SuppressWarnings("UnusedDeclaration")
@JsonIgnoreProperties(ignoreUnknown = true)
public final class ResultWrapper implements Deserialization {

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
}
