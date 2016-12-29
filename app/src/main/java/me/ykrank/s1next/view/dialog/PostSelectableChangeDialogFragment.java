package me.ykrank.s1next.view.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;

import me.ykrank.s1next.R;
import me.ykrank.s1next.widget.track.event.page.PageEndEvent;
import me.ykrank.s1next.widget.track.event.page.PageStartEvent;

/**
 * A dialog shows prompt if user's reply is not empty and want
 * to finish current Activity.
 */
public final class PostSelectableChangeDialogFragment extends BaseDialogFragment {

    public static final String TAG = PostSelectableChangeDialogFragment.class.getName();

    private static final String ARG_SELECTABLE = "selectable";

    private DialogInterface.OnClickListener positiveListener = null;

    public static PostSelectableChangeDialogFragment newInstance(boolean prependSelectable) {
        PostSelectableChangeDialogFragment fragment = new PostSelectableChangeDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(ARG_SELECTABLE, prependSelectable);
        fragment.setArguments(bundle);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        boolean prependSelectable = getArguments().getBoolean(ARG_SELECTABLE);
        String msg;
        if (prependSelectable) {
            msg = getString(R.string.dialog_message_post_selectable_warn);
        } else {
            msg = getString(R.string.dialog_message_post_unselectable_warn);
        }

        return new AlertDialog.Builder(getContext())
                .setMessage(msg)
                .setPositiveButton(R.string.dialog_message_text_switch, positiveListener)
                .setNegativeButton(android.R.string.cancel, null)
                .create();
    }

    @Override
    public void onResume() {
        super.onResume();
        trackAgent.post(new PageStartEvent(getContext(), "弹窗-帖子文本改变可选性的提醒"));
    }

    @Override
    public void onPause() {
        trackAgent.post(new PageEndEvent(getContext(), "弹窗-帖子文本改变可选性的提醒"));
        super.onPause();
    }

    public PostSelectableChangeDialogFragment setPositiveListener(DialogInterface.OnClickListener onClickListener) {
        this.positiveListener = onClickListener;
        return this;
    }
}
