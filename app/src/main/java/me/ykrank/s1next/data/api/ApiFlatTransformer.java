package me.ykrank.s1next.data.api;

import android.text.TextUtils;

import io.reactivex.Observable;
import io.reactivex.ObservableTransformer;
import io.reactivex.functions.Function;
import me.ykrank.s1next.App;
import me.ykrank.s1next.AppComponent;
import me.ykrank.s1next.data.User;
import me.ykrank.s1next.data.api.model.Account;
import me.ykrank.s1next.data.api.model.wrapper.AccountResultWrapper;
import me.ykrank.s1next.data.api.model.wrapper.OriginWrapper;
import me.ykrank.s1next.util.L;
import me.ykrank.s1next.data.api.model.wrapper.OriginWrapper;

/**
 * Created by ykrank on 2016/10/18.
 */

public class ApiFlatTransformer {

    /**
     * A rxjava transformer to judge whether server throw error
     */
    public static <T> ObservableTransformer<T, T> apiErrorTransformer() {
        return observable -> observable.flatMap(wrapper -> {
            if (wrapper instanceof OriginWrapper) {
                String error = ((OriginWrapper) wrapper).getError();
                //api error
                if (!TextUtils.isEmpty(error)) {
                    return Observable.error(new ApiException.ApiServerException(error));
                }
            }
            return createData(wrapper);
        });
    }

    /**
     * A helpers method provides authenticity token.
     *
     * @param func A function that, when applied to the authenticity token, returns an
     *             Observable. And the {@link Observable} is what we want to return
     *             if we get authenticity token successful.
     * @return Returns {@link S1Service#refreshAuthenticityToken()}'s result if we
     * failed to get authenticity token, otherwise returns {@code func.call(authenticityToken)}.
     */
    public static <T> Observable<T> flatMappedWithAuthenticityToken(
            S1Service mS1Service, UserValidator mUserValidator, User mUser,
            Function<String, Observable<T>> func) {
        String authenticityToken = mUser.getAuthenticityToken();
        if (TextUtils.isEmpty(authenticityToken)) {
            return mS1Service.refreshAuthenticityToken().flatMap(resultWrapper -> {
                Account account = resultWrapper.getData();
                // return the AccountResultWrapper if we cannot get the authenticity token
                // (if account has expired or network error)
                if (TextUtils.isEmpty(account.getAuthenticityToken())) {
                    return Observable.error(new ApiException.AuthenticityTokenException("获取登录信息错误",
                            new ApiException("AccountResultWrapper:" + resultWrapper)));
                } else {
                    mUserValidator.validate(account);
                    return func.apply(account.getAuthenticityToken());
                }
            });
        } else {
            try {
                return func.apply(authenticityToken);
            } catch (Exception e) {
                L.report(e);
                return Observable.error(e);
            }
        }
    }

    public static Observable<AccountResultWrapper> flatMappedWithAuthenticityToken(Function<String, Observable<AccountResultWrapper>> func) {
        AppComponent component = App.getAppComponent();
        return flatMappedWithAuthenticityToken(component.getS1Service(), component.getUserValidator(),
                component.getUser(), func);
    }
    private static <T> Observable<T> createData(T t) {
        return Observable.create(emitter -> {
            try {
                emitter.onNext(t);
                emitter.onComplete();
            } catch (Exception e) {
                emitter.onError(e);
            }
        });
    }
}
