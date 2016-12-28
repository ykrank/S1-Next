package me.ykrank.s1next.view.dialog;

import android.os.Bundle;

import me.ykrank.s1next.R;
import me.ykrank.s1next.data.api.model.Result;
import me.ykrank.s1next.data.api.model.wrapper.ResultWrapper;
import me.ykrank.s1next.widget.track.event.PageEndEvent;
import me.ykrank.s1next.widget.track.event.PageStartEvent;
import rx.Observable;

/**
 * A dialog requests to add thread to user's favourites.
 */
public final class ThreadFavouritesAddRequestDialogFragment
        extends ProgressDialogFragment<ResultWrapper> {

    public static final String TAG = ThreadFavouritesAddRequestDialogFragment.class.getName();

    private static final String ARG_THREAD_ID = "thread_id";
    private static final String ARG_REMARK = "remark";

    private static final String STATUS_ADD_TO_FAVOURITES_SUCCESS = "favorite_do_success";
    private static final String STATUS_ADD_TO_FAVOURITES_REPEAT = "favorite_repeat";

    public static ThreadFavouritesAddRequestDialogFragment newInstance(String threadId, String remark) {
        ThreadFavouritesAddRequestDialogFragment fragment =
                new ThreadFavouritesAddRequestDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_THREAD_ID, threadId);
        bundle.putString(ARG_REMARK, remark);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    protected Observable<ResultWrapper> getSourceObservable() {
        return flatMappedWithAuthenticityToken(s ->
                mS1Service.addThreadFavorite(s, getArguments().getString(ARG_THREAD_ID),
                        getArguments().getString(ARG_REMARK)));
    }

    @Override
    protected void onNext(ResultWrapper data) {
        Result result = data.getResult();
        if (result.getStatus().equals(STATUS_ADD_TO_FAVOURITES_SUCCESS)
                || result.getStatus().equals(STATUS_ADD_TO_FAVOURITES_REPEAT)) {
            ((ThreadFavouritesAddDialogFragment) getFragmentManager().findFragmentByTag(
                    ThreadFavouritesAddDialogFragment.TAG)).dismissAllowingStateLoss();
        }

        showShortText(result.getMessage());
    }

    @Override
    protected CharSequence getProgressMessage() {
        return getText(R.string.dialog_progress_message_favourites_add);
    }

    @Override
    public void onResume() {
        super.onResume();
        trackAgent.post(new PageStartEvent("弹窗-添加收藏进度条-" + TAG));
    }

    @Override
    public void onPause() {
        trackAgent.post(new PageEndEvent("弹窗-添加收藏进度条-" + TAG));
        super.onPause();
    }
}
