package me.ykrank.s1next.data.api

import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import me.ykrank.s1next.App
import me.ykrank.s1next.data.User
import me.ykrank.s1next.data.api.app.model.AppResult
import me.ykrank.s1next.data.api.model.wrapper.AccountResultWrapper
import me.ykrank.s1next.data.api.model.wrapper.OriginWrapper
import me.ykrank.s1next.util.L

/**
 * Created by ykrank on 2016/10/18.
 */

object ApiFlatTransformer {

    /**
     * A rxjava transformer to judge whether server throw error
     */
    fun <T> apiErrorTransformer(): ObservableTransformer<T, T> {
        return ObservableTransformer {
            it.flatMap {
                when (it) {
                    is OriginWrapper<*> -> {
                        if (!it.error.isNullOrEmpty()) {
                            return@flatMap Observable.error<T>(ApiException.ApiServerException(it.error))
                        }
                    }
                    is AppResult -> {
                        if (!it.success) {
                            return@flatMap Observable.error<T>(ApiException.AppServerException(it.message, it.code))
                        }
                    }
                }
                return@flatMap createData(it)
            }
        }
    }

    /**
     * A helpers method provides authenticity token.

     * @param func A function that, when applied to the authenticity token, returns an
     * *             Observable. And the [Observable] is what we want to return
     * *             if we get authenticity token successful.
     * *
     * @return Returns [S1Service.refreshAuthenticityToken]'s result if we
     * * failed to get authenticity token, otherwise returns `func.call(authenticityToken)`.
     */
    fun <T> flatMappedWithAuthenticityToken(
            mS1Service: S1Service, mUserValidator: UserValidator, mUser: User,
            func: (String) -> Observable<T>): Observable<T> {
        val authenticityToken: String? = mUser.authenticityToken
        if (authenticityToken.isNullOrEmpty()) {
            return mS1Service.refreshAuthenticityToken().flatMap<T> {
                val account = it.data
                // return the AccountResultWrapper if we cannot get the authenticity token
                // (if account has expired or network error)
                if (account.authenticityToken.isNullOrEmpty()) {
                    return@flatMap Observable.error <T>(ApiException.AuthenticityTokenException("获取登录信息错误",
                            ApiException("AccountResultWrapper:" + it)))
                } else {
                    mUserValidator.validate(account)
                    return@flatMap func.invoke(account.authenticityToken)
                }
            }
        } else {
            try {
                return func.invoke(authenticityToken!!)
            } catch (e: Exception) {
                L.report(e)
                return Observable.error <T>(e)
            }

        }
    }

    fun flatMappedWithAuthenticityToken(func: (String) -> Observable<AccountResultWrapper>): Observable<AccountResultWrapper> {
        val component = App.getAppComponent()
        return flatMappedWithAuthenticityToken(component.s1Service, component.userValidator,
                component.user, func)
    }

    private fun <T> createData(t: T): Observable<T> {
        return Observable.create<T> { emitter ->
            try {
                emitter.onNext(t)
                emitter.onComplete()
            } catch (e: Exception) {
                emitter.onError(e)
            }
        }
    }
}
