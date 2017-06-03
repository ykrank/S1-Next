package me.ykrank.s1next.viewmodel;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import me.ykrank.s1next.BR;
import me.ykrank.s1next.data.User;

public final class UserViewModel extends BaseObservable {

    private final User user = new ObservableUser(this);

    public User getUser() {
        return user;
    }

    @Bindable
    public boolean isSigned() {
        return user.isSigned();
    }

    private static final class ObservableUser extends User {

        private final BaseObservable mBaseObservable;

        public ObservableUser(BaseObservable baseObservable) {
            this.mBaseObservable = baseObservable;
        }

        @Override
        public void setLogged(boolean logged) {
            super.setLogged(logged);
            mBaseObservable.notifyChange();
        }

        @Override
        public void setSigned(boolean b) {
            super.setSigned(b);
            mBaseObservable.notifyPropertyChanged(BR.signed);
        }
    }
}
