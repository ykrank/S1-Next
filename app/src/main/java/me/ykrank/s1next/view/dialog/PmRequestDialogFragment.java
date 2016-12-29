package me.ykrank.s1next.view.dialog;

import android.os.Bundle;

import me.ykrank.s1next.App;
import me.ykrank.s1next.R;
import me.ykrank.s1next.data.api.model.Result;
import me.ykrank.s1next.data.api.model.wrapper.ResultWrapper;
import me.ykrank.s1next.widget.track.event.NewPmTrackEvent;
import me.ykrank.s1next.widget.track.event.page.PageEndEvent;
import me.ykrank.s1next.widget.track.event.page.PageStartEvent;
import rx.Observable;

/**
 * A dialog requests to post pm.
 */
public final class PmRequestDialogFragment extends ProgressDialogFragment<ResultWrapper> {

    public static final String TAG = PmRequestDialogFragment.class.getName();

    private static final String ARG_TO_UID = "arg_to_uid";
    private static final String ARG_MESSAGE = "message";

    private static final String STATUS_PM_SUCCESS = "do_success";

    public static PmRequestDialogFragment newInstance(String toUid, String msg) {
        App.get().getTrackAgent().post(new NewPmTrackEvent());
        
        PmRequestDialogFragment fragment = new PmRequestDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_TO_UID, toUid);
        bundle.putString(ARG_MESSAGE, msg);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    protected CharSequence getProgressMessage() {
        return getText(R.string.dialog_progress_message_reply);
    }

    @Override
    protected Observable<ResultWrapper> getSourceObservable() {
        String toUid = getArguments().getString(ARG_TO_UID);
        String msg = getArguments().getString(ARG_MESSAGE);


        return flatMappedWithAuthenticityToken(token ->
                mS1Service.postPm(token, toUid, msg));
    }

    @Override
    protected void onNext(ResultWrapper data) {
        Result result = data.getResult();
        if (result.getStatus().equals(STATUS_PM_SUCCESS)) {
            showShortTextAndFinishCurrentActivity(result.getMessage());
        } else {
            showShortText(result.getMessage());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        trackAgent.post(new PageStartEvent(getContext(), "弹窗-私信进度条"));
    }

    @Override
    public void onPause() {
        trackAgent.post(new PageEndEvent(getContext(), "弹窗-私信进度条"));
        super.onPause();
    }
}
