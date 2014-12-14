package cl.monsoon.s1next.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.os.Bundle;
import android.text.TextUtils;

import cl.monsoon.s1next.R;
import cl.monsoon.s1next.fragment.ReplyFragment;
import cl.monsoon.s1next.util.ObjectUtil;

/**
 * An Activity to send a reply.
 */
public final class ReplyActivity extends BaseActivity {

    public final static String ARG_THREAD_TITLE = "thread_title";
    public final static String ARG_THREAD_ID = "thread_id";
    public final static String ARG_QUOTE_POST_ID = "quote_post_id";

    private ReplyFragment mReplyFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity);

        setNavDrawerEnabled(false);
        setupNavCrossIcon();

        CharSequence quotePostId = getIntent().getCharSequenceExtra(ARG_QUOTE_POST_ID);
        String titlePrefix =
                TextUtils.isEmpty(quotePostId)
                        ? getString(R.string.reply_activity_title_prefix)
                        : getString(R.string.reply_activity_quote_title_prefix, quotePostId);
        setTitle(titlePrefix + getIntent().getStringExtra(ARG_THREAD_TITLE));

        Fragment fragment = getFragmentManager().findFragmentByTag(ReplyFragment.TAG);
        if (fragment == null) {
            mReplyFragment =
                    ReplyFragment.newInstance(
                            getIntent().getCharSequenceExtra(ARG_THREAD_ID),quotePostId);

            getFragmentManager().beginTransaction()
                    .replace(R.id.frame_layout, mReplyFragment, ReplyFragment.TAG).commit();
        } else {
            mReplyFragment = ObjectUtil.cast(fragment, ReplyFragment.class);
        }
    }

    /**
     * Show AlertDialog when reply content is not empty.
     */
    @Override
    public void onBackPressed() {
        if (mReplyFragment.isReplyEmpty()) {
            super.onBackPressed();
        } else {
            new BackPromptDialog().show(getFragmentManager(), BackPromptDialog.TAG);
        }
    }

    public static class BackPromptDialog extends DialogFragment {

        private static final String TAG = "back_prompt_dialog";

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            return
                    new AlertDialog.Builder(getActivity())
                            .setMessage(R.string.dialog_message_reply_back_prompt)
                            .setPositiveButton(android.R.string.ok,
                                    (dialog, which) -> getActivity().finish())
                            .setNegativeButton(
                                    android.R.string.cancel, null)
                            .create();
        }
    }
}
