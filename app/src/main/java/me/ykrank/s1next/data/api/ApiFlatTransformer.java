package me.ykrank.s1next.data.api;

import android.text.TextUtils;

import me.ykrank.s1next.App;
import me.ykrank.s1next.AppComponent;
import me.ykrank.s1next.data.User;
import me.ykrank.s1next.data.api.model.Account;
import me.ykrank.s1next.data.api.model.wrapper.ResultWrapper;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;

/**
 * Created by ykrank on 2016/10/18.
 */

public class ApiFlatTransformer {

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
            Func1<String, Observable<T>> func) {
        String authenticityToken = mUser.getAuthenticityToken();
        if (TextUtils.isEmpty(authenticityToken)) {
            return mS1Service.refreshAuthenticityToken().flatMap(resultWrapper -> {
                Account account = resultWrapper.getAccount();
                // return the ResultWrapper if we cannot get the authenticity token
                // (if account has expired or network error)
                if (TextUtils.isEmpty(account.getAuthenticityToken())) {
                    return Observable.error(new ApiException.AuthenticityTokenException("获取登录信息错误",
                            new ApiException("ResultWrapper:"+resultWrapper)));
                } else {
                    mUserValidator.validate(account);
                    return func.call(account.getAuthenticityToken());
                }
            });
        } else {
            return func.call(authenticityToken);
        }
    }

    public static Observable<ResultWrapper> flatMappedWithAuthenticityToken(Func1<String, Observable<ResultWrapper>> func) {
        AppComponent component = App.getAppComponent(App.get());
        return flatMappedWithAuthenticityToken(component.getS1Service(), component.getUserValidator(),
                component.getUser(), func);
    }

    private static <T> Observable<T> createData(T t) {
        return Observable.create(new Observable.OnSubscribe<T>() {
            @Override
            public void call(Subscriber<? super T> subscriber) {
                try {
                    subscriber.onNext(t);
                    subscriber.onCompleted();
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        });
    }
}
