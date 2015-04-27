package cl.monsoon.s1next.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.MenuItem;

import cl.monsoon.s1next.R;
import cl.monsoon.s1next.fragment.ReplyFragment;

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

        setupNavCrossIcon();

        String quotePostId = getIntent().getStringExtra(ARG_QUOTE_POST_ID);
        String titlePrefix = TextUtils.isEmpty(quotePostId)
                ? getString(R.string.reply_activity_title_prefix)
                : getString(R.string.reply_activity_quote_title_prefix,
                getIntent().getStringExtra(ARG_QUOTE_POST_COUNT));
        setTitle(titlePrefix + getIntent().getStringExtra(ARG_THREAD_TITLE));

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentByTag(ReplyFragment.TAG);
        if (fragment == null) {
            mReplyFragment = ReplyFragment.newInstance(getIntent().getStringExtra(ARG_THREAD_ID),
                    quotePostId);

            fragmentManager.beginTransaction().replace(R.id.frame_layout, mReplyFragment,
                    ReplyFragment.TAG).commit();
        } else {
            mReplyFragment = (ReplyFragment) fragment;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (mReplyFragment.isReplyEmpty()) {
                    finish();
                } else {
                    new ReplyDiscardPromptDialog().show(getSupportFragmentManager(),
                            ReplyDiscardPromptDialog.TAG);
                }

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Show AlertDialog when reply content is empty.
     */
    @Override
    public void onBackPressed() {
        if (mReplyFragment.isEmoticonKeyboardShowing()) {
            mReplyFragment.hideEmoticonKeyboard();
        } else if (mReplyFragment.isReplyEmpty()) {
            super.onBackPressed();
        } else {
            new ReplyDiscardPromptDialog().show(getSupportFragmentManager(),
                    ReplyDiscardPromptDialog.TAG);
        }
    }

    public static class ReplyDiscardPromptDialog extends DialogFragment {

        private static final String TAG = ReplyDiscardPromptDialog.class.getSimpleName();

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return new AlertDialog.Builder(getActivity())
                    .setMessage(R.string.dialog_message_reply_discard_prompt)
                    .setPositiveButton(R.string.dialog_message_text_discard,
                            (dialog, which) -> getActivity().finish())
                    .setNegativeButton(android.R.string.cancel, null)
                    .create();
        }
    }
}
