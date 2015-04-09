package cl.monsoon.s1next.event;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public final class UserStatusEvent {

    public static final int USER_LOGIN = 0;
    public static final int USER_COOKIE_EXPIRATION = 1;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({USER_LOGIN, USER_COOKIE_EXPIRATION})
    private @interface UserStatus {

    }

    private final int userStatus;

    public UserStatusEvent(@UserStatus int userStatus) {
        this.userStatus = userStatus;
    }

    public int getUserStatus() {
        return userStatus;
    }
}
