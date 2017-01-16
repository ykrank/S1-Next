package me.ykrank.s1next.view.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.WindowManager;

import javax.inject.Inject;

import me.ykrank.s1next.App;
import me.ykrank.s1next.R;
import me.ykrank.s1next.data.event.BlackListAddEvent;
import me.ykrank.s1next.databinding.DialogBlacklistRemarkBinding;
import me.ykrank.s1next.util.ViewUtil;
import me.ykrank.s1next.widget.EventBus;
import me.ykrank.s1next.widget.track.event.BlackListTrackEvent;
import me.ykrank.s1next.widget.track.event.page.PageEndEvent;
import me.ykrank.s1next.widget.track.event.page.PageStartEvent;

/**
 * A dialog lets the user enter thread link/ID to go to that thread.
 */
public final class BlackListRemarkDialogFragment extends BaseDialogFragment {

    public static final String TAG = BlackListRemarkDialogFragment.class.getName();

    private static final String ARG_AUTHOR_ID = "arg_author_id";
    private static final String ARG_AUTHOR_NAME = "arg_author_name";

    @Inject
    EventBus eventBus;

    public static BlackListRemarkDialogFragment newInstance(int authorId, String authorName) {
        BlackListRemarkDialogFragment fragment = new BlackListRemarkDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_AUTHOR_ID, authorId);
        bundle.putString(ARG_AUTHOR_NAME, authorName);
        fragment.setArguments(bundle);

        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        App.getAppComponent().inject(this);

        Activity activity = getActivity();
        DialogBlacklistRemarkBinding binding = DataBindingUtil.inflate(activity.getLayoutInflater(),
                R.layout.dialog_blacklist_remark, null, false);

        AlertDialog alertDialog = new AlertDialog.Builder(activity)
                .setTitle(R.string.menu_blacklist_add)
                .setView(binding.getRoot())
                .setPositiveButton(R.string.dialog_button_text_confirm, (dialog, which) -> {
                    int authorId = getArguments().getInt(ARG_AUTHOR_ID);
                    String authorName = getArguments().getString(ARG_AUTHOR_NAME);
                    trackAgent.post(new BlackListTrackEvent(true, String.valueOf(authorId), authorName));
                    eventBus.post(new BlackListAddEvent(authorId, authorName,
                            binding.blacklistRemark.getText().toString(), true));
                })
                .setNegativeButton(android.R.string.cancel, null)
                .create();
        alertDialog.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        ViewUtil.consumeRunnableWhenImeActionPerformed(binding.blacklistRemark, () ->
                alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).performClick());
        return alertDialog;
    }

    @Override
    public void onResume() {
        super.onResume();
        trackAgent.post(new PageStartEvent(getContext(), "弹窗-黑名单备注"));
    }

    @Override
    public void onPause() {
        trackAgent.post(new PageEndEvent(getContext(), "弹窗-黑名单备注"));
        super.onPause();
    }
}
