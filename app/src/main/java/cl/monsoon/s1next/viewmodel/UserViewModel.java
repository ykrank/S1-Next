package cl.monsoon.s1next.viewmodel;

import android.databinding.BaseObservable;

import cl.monsoon.s1next.data.User;

public final class UserViewModel extends BaseObservable {

    private final User user = new ObservableUser(this);

    public User getUser() {
        return user;
    }

    private static class ObservableUser extends User {

        private final BaseObservable mBaseObservable;

        public ObservableUser(BaseObservable baseObservable) {
            this.mBaseObservable = baseObservable;
        }

        @Override
        public void setLogged(boolean logged) {
            super.setLogged(logged);

            mBaseObservable.notifyChange();
        }
    }
}
