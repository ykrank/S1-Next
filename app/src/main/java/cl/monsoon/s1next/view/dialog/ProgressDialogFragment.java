package cl.monsoon.s1next.view.dialog;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.widget.Toast;

import cl.monsoon.s1next.App;
import cl.monsoon.s1next.data.api.S1Service;
import cl.monsoon.s1next.data.api.UserValidator;
import cl.monsoon.s1next.util.ToastUtil;
import cl.monsoon.s1next.view.fragment.BaseFragment;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
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
public abstract class ProgressDialogFragment<D> extends DialogFragment {

    S1Service mS1Service;
    private UserValidator mUserValidator;

    private Subscription mSubscription;

    @Override
    @CallSuper
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mS1Service = App.getAppComponent(getActivity()).getS1Service();
        mUserValidator = App.getAppComponent(getActivity()).getUserValidator();

        // retain this Fragment
        setRetainInstance(true);

        request();
    }

    @NonNull
    @Override
    public final Dialog onCreateDialog(Bundle savedInstanceState) {
        ProgressDialog progressDialog = new ProgressDialog(getActivity());
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
    @CallSuper
    public void onDestroy() {
        super.onDestroy();

        if (mSubscription != null) {
            mSubscription.unsubscribe();
        }
    }

    /**
     * @see BaseFragment#load()
     */
    private void request() {
        mSubscription = getSourceObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(d -> UserValidator.validateIntercept(mUserValidator, d))
                .finallyDo(this::finallyDo)
                .subscribe(this::onNext, this::onError);
    }

    /**
     * @see BaseFragment#getSourceObservable()
     */
    protected abstract Observable<D> getSourceObservable();

    /**
     * @see BaseFragment#onNext(Object)
     */
    protected abstract void onNext(D data);

    /**
     * @see BaseFragment#onError(Throwable)
     */
    protected void onError(Throwable throwable) {
        ToastUtil.showByText(throwable.toString(), Toast.LENGTH_LONG);
    }

    /**
     * @see BaseFragment#finallyDo()
     */
    protected void finallyDo() {
        dismiss();
    }

    protected abstract CharSequence getProgressMessage();
}
