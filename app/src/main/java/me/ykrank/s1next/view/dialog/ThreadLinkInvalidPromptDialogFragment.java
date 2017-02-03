package me.ykrank.s1next.view.dialog;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;

import me.ykrank.s1next.R;
import me.ykrank.s1next.util.IntentUtil;
import me.ykrank.s1next.widget.track.event.page.PageEndEvent;
import me.ykrank.s1next.widget.track.event.page.PageStartEvent;

/**
 * A dialog shows error prompt if the thread link is invalid.
 * Clicks the negative button can let user open this thread link in browser.
 */
public final class ThreadLinkInvalidPromptDialogFragment extends BaseDialogFragment {

    public static final String TAG = ThreadLinkInvalidPromptDialogFragment.class.getName();

    private static final String ARG_MESSAGE = "message";

    public static ThreadLinkInvalidPromptDialogFragment newInstance(Context context,
                                                                    String text) {
        ThreadLinkInvalidPromptDialogFragment fragment = new ThreadLinkInvalidPromptDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putCharSequence(ARG_MESSAGE, text);
        fragment.setArguments(bundle);

        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getContext())
                .setMessage(getArguments().getCharSequence(ARG_MESSAGE))
                .setPositiveButton(R.string.dialog_button_text_done, (dialog, which) -> dismiss())
                .setNegativeButton(R.string.dialog_button_text_use_a_different_app, (dialog, which) ->
                        IntentUtil.startViewIntentExcludeOurApp(getContext(),
                                getActivity().getIntent().getData()))
                .create();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);

        // getActivity() = null when configuration changes (like orientation changes)
        if (getActivity() != null) {
            getActivity().finish();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        trackAgent.post(new PageStartEvent(getContext(), "弹窗-链接解析出错提醒-"));
    }

    @Override
    public void onPause() {
        trackAgent.post(new PageEndEvent(getContext(), "弹窗-链接解析出错提醒-"));
        super.onPause();
    }
}
