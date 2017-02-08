package me.ykrank.s1next.view.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.widget.Toast;

import me.ykrank.s1next.App;
import me.ykrank.s1next.AppComponent;
import me.ykrank.s1next.data.User;
import me.ykrank.s1next.data.api.ApiFlatTransformer;
import me.ykrank.s1next.data.api.S1Service;
import me.ykrank.s1next.data.api.UserValidator;
import me.ykrank.s1next.util.ErrorUtil;
import me.ykrank.s1next.util.RxJavaUtil;
import me.ykrank.s1next.view.activity.BaseActivity;
import me.ykrank.s1next.view.fragment.BaseRecyclerViewFragment;
import me.ykrank.s1next.view.internal.CoordinatorLayoutAnchorDelegate;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * A dialog shows {@link ProgressDialog}.
 * Also wraps some related methods to request data.
 * <p>
 * This {@link DialogFragment} is retained in order
 * to retain request when configuration changes.
 *
 * @param <D> The data we want to request.
 */
abstract class ProgressDialogFragment<D> extends BaseDialogFragment {

    S1Service mS1Service;

    UserValidator mUserValidator;

    private User mUser;

    private Subscription mSubscription;

    @Override
    @CallSuper
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppComponent appComponent = App.getAppComponent();
        mS1Service = appComponent.getS1Service();
        mUser = appComponent.getUser();
        mUserValidator = appComponent.getUserValidator();

        // retain this Fragment
        setRetainInstance(true);

        request();
    }

    @NonNull
    @Override
    public final Dialog onCreateDialog(Bundle savedInstanceState) {
        ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage(getProgressMessage());

        return progressDialog;
    }

    @Override
    @CallSuper
    public void onDestroyView() {
        // see https://code.google.com/p/android/issues/detail?id=17423
        Dialog dialog = getDialog();
        if (dialog != null) {
            getDialog().setOnDismissListener(null);
        }

        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        RxJavaUtil.unsubscribeIfNotNull(mSubscription);
    }

    /**
     * @see BaseRecyclerViewFragment#load()
     */
    private void request() {
        mSubscription = getSourceObservable().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doAfterTerminate(this::finallyDo)
                .subscribe(this::onNext, this::onError);
    }

    /**
     * @see BaseRecyclerViewFragment#getSourceObservable()
     */
    abstract Observable<D> getSourceObservable();

    /**
     * @see ApiFlatTransformer#flatMappedWithAuthenticityToken(S1Service, UserValidator, User, Func1)
     */
    final Observable<D> flatMappedWithAuthenticityToken(Func1<String, Observable<D>> func) {
        return ApiFlatTransformer.flatMappedWithAuthenticityToken(mS1Service, mUserValidator, mUser, func);
    }

    /**
     * @see BaseRecyclerViewFragment#onNext(Object)
     */
    abstract void onNext(D data);

    /**
     * @see BaseRecyclerViewFragment#onError(Throwable)
     */
    void onError(Throwable throwable) {
        showShortText(ErrorUtil.parse(getContext(), throwable));
    }

    /**
     * @see BaseRecyclerViewFragment#finallyDo()
     */
    private void finallyDo() {
        dismissAllowingStateLoss();
    }

    /**
     * @see me.ykrank.s1next.view.activity.BaseActivity#showShortText(CharSequence)
     */
    final void showShortText(CharSequence text) {
        ((CoordinatorLayoutAnchorDelegate) getActivity()).showShortText(text);
    }

    /**
     * If current {@link android.app.Activity} is visible, sets result message to {@link Activity}
     * in order to show a short {@link Snackbar} for message during {@link #onActivityResult(int, int, Intent)},
     * otherwise show a short {@link android.widget.Toast}.
     *
     * @param text The text to show.
     * @see BaseActivity#onActivityResult(int, int, Intent)
     */
    final void showShortTextAndFinishCurrentActivity(CharSequence text) {
        Activity activity = getActivity();
        App app = (App) activity.getApplicationContext();
        // Because Activity#onActivityResult(int, int, Intent) is always invoked when current app
        // is running in the foreground (so we are unable to set result message to Activity to
        // let BaseActivity to show a Toast if our app is running in the background).
        // So we need to handle it by ourselves.
        if (app.isAppVisible()) {
            BaseActivity.setResultMessage(activity, text);
        } else {
            Toast.makeText(app, text, Toast.LENGTH_SHORT).show();
        }

        activity.finish();
    }

    protected abstract CharSequence getProgressMessage();
}
