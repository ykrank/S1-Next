package cl.monsoon.s1next.view.dialog;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.widget.Toast;

import com.trello.rxlifecycle.FragmentEvent;
import com.trello.rxlifecycle.components.support.RxDialogFragment;

import cl.monsoon.s1next.App;
import cl.monsoon.s1next.data.api.S1Service;
import cl.monsoon.s1next.data.api.UserValidator;
import cl.monsoon.s1next.util.ToastUtil;
import cl.monsoon.s1next.view.fragment.BaseFragment;
import rx.Observable;
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
public abstract class ProgressDialogFragment<D> extends RxDialogFragment {

    S1Service mS1Service;
    private UserValidator mUserValidator;

    @Override
    @CallSuper
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.AppComponent appComponent = App.getAppComponent(getActivity());
        mS1Service = appComponent.getS1Service();
        mUserValidator = appComponent.getUserValidator();

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

    /**
     * @see BaseFragment#load()
     */
    private void request() {
        getSourceObservable().compose(bindUntilEvent(FragmentEvent.DESTROY))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(d -> UserValidator.validateIntercept(mUserValidator, d))
                .finallyDo(this::finallyDo)
                .subscribe(this::onNext, this::onError);
    }

    /**
     * @see BaseFragment#getSourceObservable()
     */
    abstract Observable<D> getSourceObservable();

    /**
     * @see BaseFragment#onNext(Object)
     */
    abstract void onNext(D data);

    /**
     * @see BaseFragment#onError(Throwable)
     */
    void onError(Throwable throwable) {
        ToastUtil.showByText(throwable.toString(), Toast.LENGTH_LONG);
    }

    /**
     * @see BaseFragment#finallyDo()
     */
    void finallyDo() {
        dismissAllowingStateLoss();
    }

    protected abstract CharSequence getProgressMessage();
}
