package me.ykrank.s1next.view.dialog;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;

import io.reactivex.Observable;
import io.reactivex.functions.Function;
import me.ykrank.s1next.util.L;

/**
 * A {@link ProgressDialogFragment} subscribe a observable
 */
public final class SimpleProgressDialogFragment<D> extends ProgressDialogFragment<D> {

    public static final String TAG = SimpleProgressDialogFragment.class.getName();

    private static final String ARG_PROGRESS_MSG = "progress_msg";
    private static final String ARG_FINISH_ACTIVITY_AFTER_COMPLETE = "finish_activity_after_complete";

    private Observable<D> sourceObservable;
    @Nullable
    private Function<D, CharSequence> onNextAction;
    @Nullable
    private CharSequence progressMsg;
    private boolean finishActivity;

    /**
     * start a progress dialog to subscribe a observable and show msg (or not) after subscribed
     *
     * @param progressMsg      Message show in progress dialog
     * @param sourceObservable source provider
     * @param onNext           do after data arrived, return toast message
     * @param finishActivity   whether finish current activity after subscribed
     * @param cancelable       whether dialog cancelable on touch outside
     * @param <D>              data
     */
    public static <D> void start(@NonNull FragmentManager fm, @Nullable CharSequence progressMsg, @NonNull Observable<D> sourceObservable,
                                 @Nullable Function<D, CharSequence> onNext, boolean finishActivity, boolean cancelable) {
        SimpleProgressDialogFragment<D> fragment = new SimpleProgressDialogFragment<>();
        Bundle bundle = new Bundle();
        bundle.putCharSequence(ARG_PROGRESS_MSG, progressMsg);
        bundle.putBoolean(ARG_FINISH_ACTIVITY_AFTER_COMPLETE, finishActivity);
        bundle.putBoolean(ARG_DIALOG_NOT_CANCELABLE_ON_TOUCH_OUTSIDE, !cancelable);
        fragment.setArguments(bundle);

        fragment.setSourceObservable(sourceObservable);
        fragment.setOnNextAction(onNext);

        fragment.show(fm, SimpleProgressDialogFragment.TAG);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        progressMsg = getArguments().getCharSequence(ARG_PROGRESS_MSG);
        finishActivity = getArguments().getBoolean(ARG_FINISH_ACTIVITY_AFTER_COMPLETE);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (sourceObservable == null) {
            L.report(new IllegalStateException("SourceObservable is null when SimpleProgressDialogFragment onResume"));
            getFragmentManager().beginTransaction().remove(this).commitAllowingStateLoss();
        }
    }

    @Override
    protected Observable<D> getSourceObservable() {
        return sourceObservable;
    }

    @Override
    protected void onNext(D data) {
        if (onNextAction != null) {
            try {
                CharSequence msg = onNextAction.apply(data);
                if (finishActivity) {
                    showShortTextAndFinishCurrentActivity(msg);
                } else {
                    showShortText(msg);
                }
            } catch (Exception e) {
                L.report(e);
                if (finishActivity && getActivity() != null) {
                    getActivity().finish();
                }
            }
        } else if (finishActivity) {
            getActivity().finish();
        }
    }

    @Override
    protected CharSequence getProgressMessage() {
        return progressMsg;
    }

    public void setSourceObservable(Observable<D> sourceObservable) {
        this.sourceObservable = sourceObservable;
    }

    public void setOnNextAction(@Nullable Function<D, CharSequence> onNextAction) {
        this.onNextAction = onNextAction;
    }
}
