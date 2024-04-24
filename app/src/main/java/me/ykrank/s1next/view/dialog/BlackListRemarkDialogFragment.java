package me.ykrank.s1next.view.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import android.view.WindowManager;

import com.github.ykrank.androidtools.util.RxJavaUtil;
import com.github.ykrank.androidtools.util.ViewUtil;
import com.github.ykrank.androidtools.widget.RxBus;

import javax.inject.Inject;

import me.ykrank.s1next.App;
import me.ykrank.s1next.R;
import me.ykrank.s1next.data.db.biz.BlackListBiz;
import me.ykrank.s1next.databinding.DialogBlacklistRemarkBinding;
import me.ykrank.s1next.view.event.BlackListChangeEvent;
import me.ykrank.s1next.widget.track.event.BlackListTrackEvent;

/**
 * A dialog lets the user enter blacklist remark.
 */
public final class BlackListRemarkDialogFragment extends BaseDialogFragment {

    public static final String TAG = BlackListRemarkDialogFragment.class.getName();

    private static final String ARG_AUTHOR_ID = "arg_author_id";
    private static final String ARG_AUTHOR_NAME = "arg_author_name";

    @Inject
    RxBus rxBus;

    @Inject
    BlackListBiz blackListDb;

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
        App.Companion.getAppComponent().inject(this);

        Activity activity = getActivity();
        DialogBlacklistRemarkBinding binding = DataBindingUtil.inflate(activity.getLayoutInflater(),
                R.layout.dialog_blacklist_remark, null, false);

        AlertDialog alertDialog = new AlertDialog.Builder(activity)
                .setTitle(R.string.menu_blacklist_add)
                .setView(binding.getRoot())
                .setPositiveButton(R.string.dialog_button_text_confirm, (dialog, which) -> {
                    int authorId = getArguments().getInt(ARG_AUTHOR_ID);
                    String authorName = getArguments().getString(ARG_AUTHOR_NAME);
                    String remark = binding.blacklistRemark.getText().toString();
                    getTrackAgent().post(new BlackListTrackEvent(true, String.valueOf(authorId), authorName));
                    RxJavaUtil.workWithUiThread(() -> blackListDb.saveDefaultBlackList(authorId, authorName, remark),
                            () -> rxBus.post(new BlackListChangeEvent(authorId, authorName, remark, true)));
                })
                .setNegativeButton(android.R.string.cancel, null)
                .create();
        alertDialog.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        ViewUtil.consumeRunnableWhenImeActionPerformed(binding.blacklistRemark, () ->
                alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).performClick());
        return alertDialog;
    }
}
