package me.ykrank.s1next.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;

import io.reactivex.ObservableTransformer;
import io.reactivex.SingleTransformer;
import me.ykrank.s1next.App;

public final class JsonUtil {


    public static <D> ObservableTransformer<String, D> jsonTransformer(Class<D> dClass) {
        return observable -> observable.map(s -> App.Companion.getAppComponent().getJsonMapper().readValue(s, dClass));
    }

    public static <D> ObservableTransformer<String, D> jsonTransformer(JavaType javaType) {
        return observable -> observable.map(s -> App.Companion.getAppComponent().getJsonMapper().readValue(s, javaType));
    }

    public static <D> ObservableTransformer<String, D> jsonTransformer(TypeReference<D> typeReference) {
        return observable -> observable.map(s -> App.Companion.getAppComponent().getJsonMapper().readValue(s, typeReference));
    }

    public static <D> SingleTransformer<String, D> jsonSingleTransformer(Class<D> dClass) {
        return observable -> observable.map(s -> App.Companion.getAppComponent().getJsonMapper().readValue(s, dClass));
    }

    public static <D> SingleTransformer<String, D> jsonSingleTransformer(JavaType javaType) {
        return observable -> observable.map(s -> App.Companion.getAppComponent().getJsonMapper().readValue(s, javaType));
    }

    public static <D> SingleTransformer<String, D> jsonSingleTransformer(TypeReference<D> typeReference) {
        return observable -> observable.map(s -> App.Companion.getAppComponent().getJsonMapper().readValue(s, typeReference));
    }
}
