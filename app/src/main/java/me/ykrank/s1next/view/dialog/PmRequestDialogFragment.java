package me.ykrank.s1next.view.dialog;

import android.os.Bundle;

import javax.inject.Inject;

import io.reactivex.Observable;
import me.ykrank.s1next.App;
import me.ykrank.s1next.R;
import me.ykrank.s1next.data.api.model.Result;
import me.ykrank.s1next.data.api.model.wrapper.AccountResultWrapper;
import me.ykrank.s1next.widget.EditorDiskCache;
import me.ykrank.s1next.widget.track.event.NewPmTrackEvent;

/**
 * A dialog requests to post pm.
 */
public final class PmRequestDialogFragment extends ProgressDialogFragment<AccountResultWrapper> {

    public static final String TAG = PmRequestDialogFragment.class.getName();

    @Inject
    EditorDiskCache editorDiskCache;

    private static final String ARG_TO_UID = "arg_to_uid";
    private static final String ARG_MESSAGE = "message";
    private static final String ARG_CACHE_KEY = "cache_key";

    private static final String STATUS_PM_SUCCESS = "do_success";

    private String cacheKey;

    public static PmRequestDialogFragment newInstance(String toUid, String msg, String cacheKey) {
        App.get().getTrackAgent().post(new NewPmTrackEvent());
        
        PmRequestDialogFragment fragment = new PmRequestDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_TO_UID, toUid);
        bundle.putString(ARG_MESSAGE, msg);
        bundle.putString(ARG_CACHE_KEY, cacheKey);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.getAppComponent().inject(this);
    }

    @Override
    protected CharSequence getProgressMessage() {
        return getText(R.string.dialog_progress_message_reply);
    }

    @Override
    protected Observable<AccountResultWrapper> getSourceObservable() {
        String toUid = getArguments().getString(ARG_TO_UID);
        String msg = getArguments().getString(ARG_MESSAGE);
        cacheKey = getArguments().getString(ARG_CACHE_KEY);

        return flatMappedWithAuthenticityToken(token ->
                mS1Service.postPm(token, toUid, msg));
    }

    @Override
    protected void onNext(AccountResultWrapper data) {
        Result result = data.getResult();
        if (result.getStatus().equals(STATUS_PM_SUCCESS)) {
            editorDiskCache.remove(cacheKey);
            showShortTextAndFinishCurrentActivity(result.getMessage());
        } else {
            showShortText(result.getMessage());
        }
    }
}
