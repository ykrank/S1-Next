package me.ykrank.s1next.data.api;

import androidx.annotation.Nullable;
import android.text.TextUtils;

import com.google.common.base.Objects;
import com.github.ykrank.androidtools.util.L;

import me.ykrank.s1next.App;
import me.ykrank.s1next.data.User;
import me.ykrank.s1next.data.api.app.model.AppLoginResult;
import me.ykrank.s1next.data.api.app.model.AppUserInfo;
import me.ykrank.s1next.data.api.model.Account;
import me.ykrank.s1next.data.api.model.wrapper.BaseDataWrapper;
import me.ykrank.s1next.data.api.model.wrapper.BaseResultWrapper;
import me.ykrank.s1next.task.AutoSignTask;

public final class UserValidator {

    private static final String INVALID_UID = "0";

    private final User mUser;
    private final AutoSignTask mAutoSignTask;

    public UserValidator(User user, AutoSignTask autoSignTask) {
        this.mUser = user;
        this.mAutoSignTask = autoSignTask;
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
                mUser.setSigned(false);
            }
        } else {
            if (!logged) {
                // if account has logged
                mUser.setUid(uid);
                mUser.setName(account.getUsername());
                mUser.setLogged(true);
                mUser.setSigned(false);
            }
        }
        mUser.setPermission(account.getPermission());
        mUser.setAuthenticityToken(account.getAuthenticityToken());

        if (mUser.isLogged()) {
            L.setUser(mUser.getUid(), mUser.getName());
            if (mAutoSignTask.getLastCheck() == 0) {
                mAutoSignTask.silentCheck();
            }
        }
        App.Companion.get().getTrackAgent().setUser(mUser);
    }

    /**
     * validate user app signed info
     *
     * @return whether app signed
     */
    public boolean validateAppUserInfo(@Nullable AppUserInfo appUserInfo) {
        if (appUserInfo == null) {
            return false;
        }
        if (!Objects.equal(appUserInfo.isSigned(), mUser.isSigned())) {
            mUser.setSigned(appUserInfo.isSigned());
            return true;
        }
        return false;
    }

    /**
     * Checks current user's app login info and updates {@link User}'s in our app
     *
     * @return whether app login info valid
     */
    public boolean validateAppLoginInfo(@Nullable AppLoginResult loginResult) {
        if (loginResult == null) {
            return false;
        }

        if (mUser.isLogged() && TextUtils.equals(mUser.getUid(), loginResult.getUid())) {
            mUser.setAppSecureToken(loginResult.getSecureToken());
            return true;
        }
        return false;
    }
}
