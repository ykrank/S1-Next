package me.ykrank.s1next.data.api;

import android.text.TextUtils;

import com.bugsnag.android.Bugsnag;

import me.ykrank.s1next.App;
import me.ykrank.s1next.data.User;
import me.ykrank.s1next.data.api.model.Account;
import me.ykrank.s1next.data.api.model.wrapper.BaseDataWrapper;
import me.ykrank.s1next.data.api.model.wrapper.BaseResultWrapper;

public final class UserValidator {

    private static final String INVALID_UID = "0";

    private final User mUser;

    public UserValidator(User user) {
        this.mUser = user;
    }

    /**
     * Intercepts the data in order to check whether current user's login status
     * has changed and update user's status if needed.
     *
     * @param d   The data we want to intercept.
     * @param <D> The data type.
     * @return Original data.
     */
    public <D> D validateIntercept(D d) {
        Account account = null;
        if (d instanceof BaseDataWrapper) {
            account = ((BaseDataWrapper) d).getData();
        } else if (d instanceof BaseResultWrapper) {
            account = ((BaseResultWrapper) d).getData();
        }

        if (account != null) {
            validate(account);
        }

        return d;
    }

    /**
     * Checks current user's login status and updates {@link User}'s in our app.
     */
    public void validate(Account account) {
        final boolean logged = mUser.isLogged();
        String uid = account.getUid();
        if (INVALID_UID.equals(uid) || TextUtils.isEmpty(uid)) {
            if (logged) {
                // if account has expired
                mUser.setUid(null);
                mUser.setName(null);
                mUser.setLogged(false);
            }
        } else {
            if (!logged) {
                // if account has logged
                mUser.setUid(uid);
                mUser.setName(account.getUsername());
                mUser.setLogged(true);
            }
        }
        mUser.setPermission(account.getPermission());
        mUser.setAuthenticityToken(account.getAuthenticityToken());

        if (mUser.isLogged()) {
            Bugsnag.setUser(mUser.getUid(), null, mUser.getName());
        }
        App.get().getTrackAgent().setUser(mUser);
    }
}
