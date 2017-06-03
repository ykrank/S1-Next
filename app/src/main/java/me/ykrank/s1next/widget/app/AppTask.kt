package me.ykrank.s1next.widget.app

import io.reactivex.Observable
import me.ykrank.s1next.data.User
import me.ykrank.s1next.data.api.ApiFlatTransformer
import me.ykrank.s1next.data.api.UserValidator
import me.ykrank.s1next.data.api.app.AppService
import me.ykrank.s1next.util.RxJavaUtil

/**
 * Created by ykrank on 2017/6/3.
 */
class AppTask(val appService: AppService, val user: User, val userValidator: UserValidator) {

    /**
     * sign
     */
    fun check(): Observable<Boolean>? {
        if (user.isAppLogged) {
            return appService.getUserInfo(user.uid)
                    .compose(ApiFlatTransformer.appDataErrorTransformer())
                    .flatMap {
                        var result: Observable<Boolean>? = Observable.just(false)
                        if (!it.isSigned) {
                            result = sign()
                        }
                        result
                    }
        }
        return Observable.just(false)
    }

    fun invalidUserInfo() {
        appService.getUserInfo(user.uid)
                .compose(RxJavaUtil.iOTransformer())
                .doOnNext { }
    }

    /**
     * sign in app server
     * Throw {@link AppServerException} in observable if response not success
     * @return whether sign success
     */
    fun sign(): Observable<Boolean>? {
        if (user.isAppLogged) {
            return appService.sign(user.uid, user.appSecureToken)
                    .compose(ApiFlatTransformer.appApiErrorTransformer())
                    .map { it.isSuccess }
        }
        return Observable.just(false)
    }
}
