package me.ykrank.s1next.view.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;

import me.ykrank.s1next.R;
import me.ykrank.s1next.widget.EditorDiskCache;
import me.ykrank.s1next.widget.track.event.page.PageEndEvent;
import me.ykrank.s1next.widget.track.event.page.PageStartEvent;

/**
 * A dialog shows prompt if user's reply is not empty and want
 * to finish current Activity.
 */
public final class DiscardEditPromptDialogFragment extends BaseDialogFragment {

    public static final String TAG = DiscardEditPromptDialogFragment.class.getName();

    private static final String ARG_KEY = "key";
    private static final String ARG_CONTENT = "content";
    private static final String ARG_MESSAGE = "message";

    /**
     * show dialog when discard edit
     *
     * @param key     unique key to identify cache edit content
     * @param content edit content
     * @param msg     message show in dialog
     */
    public static DiscardEditPromptDialogFragment newInstance(String key, String content, String msg) {
        DiscardEditPromptDialogFragment fragment = new DiscardEditPromptDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_KEY, key);
        bundle.putString(ARG_CONTENT, content);
        bundle.putString(ARG_MESSAGE, msg);
        fragment.setArguments(bundle);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String msg = getArguments().getString(ARG_MESSAGE);
        if (msg == null) msg = getString(R.string.dialog_message_reply_discard_prompt);
        final String key = getArguments().getString(ARG_KEY);
        final String content = getArguments().getString(ARG_CONTENT);

        return new AlertDialog.Builder(getContext())
                .setMessage(msg)
                .setPositiveButton(R.string.dialog_message_text_discard, (dialog, which) -> {
                            new Thread(() -> EditorDiskCache.put(key, content)).start();
                            getActivity().finish();
                        }
                )
                .setNegativeButton(android.R.string.cancel, null)
                .create();
    }

    @Override
    public void onResume() {
        super.onResume();
        trackAgent.post(new PageStartEvent(getContext(), "弹窗-忽略编辑"));
    }

    @Override
    public void onPause() {
        trackAgent.post(new PageEndEvent(getContext(), "弹窗-忽略编辑"));
        super.onPause();
    }
}
