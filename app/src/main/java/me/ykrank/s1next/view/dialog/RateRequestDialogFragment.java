package me.ykrank.s1next.view.dialog;

import android.os.Bundle;
import android.support.annotation.NonNull;

import io.reactivex.Observable;
import me.ykrank.s1next.App;
import me.ykrank.s1next.R;
import me.ykrank.s1next.data.api.model.RatePreInfo;
import me.ykrank.s1next.data.api.model.RateResult;
import me.ykrank.s1next.widget.track.event.NewRateTrackEvent;
import me.ykrank.s1next.widget.track.event.page.PageEndEvent;
import me.ykrank.s1next.widget.track.event.page.PageStartEvent;

/**
 * A dialog requests to reply to post.
 */
public final class RateRequestDialogFragment extends ProgressDialogFragment<RateResult> {

    public static final String TAG = RateRequestDialogFragment.class.getName();

    private static final String ARG_RATE_PRE_INFO = "rate_pre_info";
    private static final String ARG_SCORE = "score";
    private static final String ARG_REASON = "reason";

    private static final String STATUS_REPLY_SUCCESS = "post_reply_succeed";

    public static RateRequestDialogFragment newInstance(@NonNull RatePreInfo ratePreInfo, String score, String reason) {
        App.get().getTrackAgent().post(new NewRateTrackEvent(ratePreInfo.getTid(), ratePreInfo.getPid(), score, reason));

        RateRequestDialogFragment fragment = new RateRequestDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(ARG_RATE_PRE_INFO, ratePreInfo);
        bundle.putString(ARG_SCORE, score);
        bundle.putString(ARG_REASON, reason);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    protected CharSequence getProgressMessage() {
        return getText(R.string.dialog_progress_message_reply);
    }

    @Override
    protected Observable<RateResult> getSourceObservable() {
        RatePreInfo ratePreInfo = getArguments().getParcelable(ARG_RATE_PRE_INFO);
        String score = getArguments().getString(ARG_SCORE);
        String reason = getArguments().getString(ARG_REASON);
        if (ratePreInfo == null) {
            return Observable.error(new IllegalStateException("RatePreInfo is null"));
        }
        return mS1Service.rate(ratePreInfo.getFormHash(), ratePreInfo.getTid(), ratePreInfo.getPid(),
                ratePreInfo.getRefer(), ratePreInfo.getHandleKey(), score, reason)
                .map(RateResult::fromHtml);
    }

    @Override
    protected void onNext(RateResult data) {
        if (data.isSuccess()) {
            showShortTextAndFinishCurrentActivity(getString(R.string.rate_success));
        } else {
            showShortText(data.getErrorMsg());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        trackAgent.post(new PageStartEvent(getContext(), "弹窗-回复进度条-"));
    }

    @Override
    public void onPause() {
        trackAgent.post(new PageEndEvent(getContext(), "弹窗-回复进度条-"));
        super.onPause();
    }
}
