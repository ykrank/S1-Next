package me.ykrank.s1next.viewmodel

import android.databinding.BaseObservable
import android.databinding.Bindable

import me.ykrank.s1next.BR
import me.ykrank.s1next.data.User
import me.ykrank.s1next.data.pref.AppDataPreferencesManager

class UserViewModel(appDataPref: AppDataPreferencesManager) : BaseObservable() {

    val user: User = ObservableUser(this, appDataPref)

    val isSigned: Boolean
        @Bindable
        get() = user.isSigned

    val isAuthor: Boolean
        @Bindable
        get() = user.uid == "223963"

    private class ObservableUser(private val mBaseObservable: BaseObservable,
                                 appDataPref: AppDataPreferencesManager) : User(appDataPref) {

        override var isLogged: Boolean
            get() = super.isLogged
            set(logged) {
                super.isLogged = logged
                mBaseObservable.notifyChange()
            }

        override var isSigned: Boolean
            get() = super.isSigned
            set(b) {
                super.isSigned = b
                mBaseObservable.notifyPropertyChanged(BR.signed)
            }
    }
}
