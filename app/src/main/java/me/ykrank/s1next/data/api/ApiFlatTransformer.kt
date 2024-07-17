package me.ykrank.s1next.data.api

import com.github.ykrank.androidtools.util.L
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.SingleTransformer
import me.ykrank.s1next.App
import me.ykrank.s1next.data.User
import me.ykrank.s1next.data.api.app.model.AppResult
import me.ykrank.s1next.data.api.model.wrapper.AccountResultWrapper
import me.ykrank.s1next.data.api.model.wrapper.OriginWrapper

/**
 * Created by ykrank on 2016/10/18.
 */

object ApiFlatTransformer {

    /**
     * A rxjava transformer to judge whether server throw error
     */
    fun <T> apiErrorTransformer(): SingleTransformer<T, T> {
        return SingleTransformer {
            it.flatMap {
                val error = getApiResultError(it)
                if (error != null) {
                    return@flatMap Single.error<T>(error)
                }
                return@flatMap createData(it)
            }
        }
    }

    /**
     * A rxjava transformer to judge whether server throw error in pair wrapped data
     */
    fun <T1, T2> apiPairErrorTransformer(): SingleTransformer<kotlin.Pair<T1, T2>, kotlin.Pair<T1, T2>> {
        return SingleTransformer {
            it.flatMap {
                var error = getApiResultError(it.first)
                if (error == null) {
                    error = getApiResultError(it.second)
                }
                if (error != null) {
                    return@flatMap Single.error<kotlin.Pair<T1, T2>>(error)
                }
                return@flatMap createData(it)
            }
        }
    }


    fun getApiResultError(it: Any?): ApiException? {
        when (it) {
            is OriginWrapper<*> -> {
                if (!it.error.isNullOrEmpty()) {
                    return ApiException.ApiServerException(it.error)
                }
            }
            is AppResult -> {
                if (!it.success) {
                    return ApiException.AppServerException(it.message, it.code)
                }
            }
        }
        return null
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
            func: (String) -> Single<T>): Single<T> {
        val authenticityToken: String? = mUser.authenticityToken
        if (authenticityToken.isNullOrEmpty()) {
            return mS1Service.refreshAuthenticityToken().flatMap<T> {
                val account = it.data
                // return the AccountResultWrapper if we cannot get the authenticity token
                // (if account has expired or network error)
                val newToken = account?.authenticityToken
                if (newToken.isNullOrEmpty()) {
                    return@flatMap Single.error<T>(ApiException.AuthenticityTokenException("获取登录信息错误",
                            ApiException("AccountResultWrapper:$it")))
                } else {
                    mUserValidator.validate(account)
                    return@flatMap func.invoke(newToken)
                }
            }
        } else {
            return try {
                func.invoke(authenticityToken)
            } catch (e: Exception) {
                L.report(e)
                Single.error<T>(e)
            }

        }
    }

    fun flatMappedWithAuthenticityToken(func: (String) -> Single<AccountResultWrapper>): Single<AccountResultWrapper> {
        val component = App.appComponent
        return flatMappedWithAuthenticityToken(component.s1Service, component.userValidator,
                component.user, func)
    }

    private fun <T> createData(t: T): Single<T> {
        return Single.just(t)
    }
}
