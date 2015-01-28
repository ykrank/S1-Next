package cl.monsoon.s1next.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;

import cl.monsoon.s1next.R;
import cl.monsoon.s1next.fragment.ReplyFragment;
import cl.monsoon.s1next.util.ObjectUtil;

/**
 * An Activity which used to send a reply.
 */
public final class ReplyActivity extends BaseActivity {

    public static final String ARG_THREAD_ID = "thread_id";
    public static final String ARG_THREAD_TITLE = "thread_title";

    public static final String ARG_QUOTE_POST_ID = "quote_post_id";
    public static final String ARG_QUOTE_POST_COUNT = "quote_post_count";

    private ReplyFragment mReplyFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity);

        setNavDrawerEnabled(false);
        setupNavCrossIcon();

        String quotePostId = getIntent().getStringExtra(ARG_QUOTE_POST_ID);
        String titlePrefix =
                TextUtils.isEmpty(quotePostId)
                        ? getString(R.string.reply_activity_title_prefix)
                        : getString(
                                R.string.reply_activity_quote_title_prefix,
                                getIntent().getStringExtra(ARG_QUOTE_POST_COUNT));
        setTitle(titlePrefix + getIntent().getStringExtra(ARG_THREAD_TITLE));

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentByTag(ReplyFragment.TAG);
        if (fragment == null) {
            mReplyFragment =
                    ReplyFragment.newInstance(
                            getIntent().getStringExtra(ARG_THREAD_ID), quotePostId);

            fragmentManager.beginTransaction()
                    .replace(R.id.frame_layout, mReplyFragment, ReplyFragment.TAG).commit();
        } else {
            mReplyFragment = ObjectUtil.cast(fragment, ReplyFragment.class);
        }
    }

    /**
     * Show AlertDialog when reply content is empty.
     */
    @Override
    public void onBackPressed() {
        if (mReplyFragment.isReplyEmpty()) {
            super.onBackPressed();
        } else {
            new BackPromptDialog().show(getSupportFragmentManager(), BackPromptDialog.TAG);
        }
    }

    public static class BackPromptDialog extends DialogFragment {

        private static final String TAG = "back_prompt_dialog";

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            return
                    new AlertDialog.Builder(getActivity())
                            .setMessage(R.string.dialog_message_reply_back_prompt)
                            .setPositiveButton(android.R.string.ok,
                                    (dialog, which) -> getActivity().finish())
                            .setNegativeButton(android.R.string.cancel, null)
                            .create();
        }
    }
}
