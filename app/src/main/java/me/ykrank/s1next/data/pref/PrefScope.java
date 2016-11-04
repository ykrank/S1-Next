package me.ykrank.s1next.data.pref;

import java.lang.annotation.Retention;

import javax.inject.Scope;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by ykrank on 2016/11/4.
 */
@Scope
@Retention(RUNTIME)
public @interface PrefScope {
}
