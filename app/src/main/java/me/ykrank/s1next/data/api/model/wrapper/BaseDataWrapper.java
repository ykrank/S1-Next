package me.ykrank.s1next.data.api.model.wrapper;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import me.ykrank.s1next.data.api.model.Account;

@SuppressWarnings("UnusedDeclaration")
@JsonIgnoreProperties(ignoreUnknown = true)
public class BaseDataWrapper<T extends Account> extends OriginWrapper<T> {

}
