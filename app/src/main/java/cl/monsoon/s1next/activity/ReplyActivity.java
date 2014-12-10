package cl.monsoon.s1next.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.os.Bundle;

import cl.monsoon.s1next.R;
import cl.monsoon.s1next.fragment.ReplyFragment;

/**
 * An Activity to send a reply.
 */
public final class ReplyActivity extends BaseActivity {

    public final static String ARG_THREAD_TITLE = "thread_title";
    public final static String ARG_THREAD_ID = "thread_id";

    private ReplyFragment mReplyFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity);

        setNavDrawerEnabled(false);
        setupNavCrossIcon();

        setTitle(
                getText(R.string.reply_activity_title_prefix)
                        + getIntent().getStringExtra(ARG_THREAD_TITLE));

        String url = getIntent().getStringExtra(ARG_THREAD_ID);

        Fragment fragment = getFragmentManager().findFragmentByTag(ReplyFragment.TAG);
        if (fragment == null) {
            mReplyFragment = ReplyFragment.newInstance(url);

            getFragmentManager().beginTransaction()
                    .replace(R.id.frame_layout, mReplyFragment, ReplyFragment.TAG).commit();
        } else {
            if (fragment instanceof ReplyFragment) {
                mReplyFragment = (ReplyFragment) fragment;
            } else {
                throw new ClassCastException(fragment + " must extend ReplyFragment.");
            }
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
                            .setMessage(R.string.dialog_message_back_prompt)
                            .setPositiveButton(android.R.string.ok,
                                    (dialog, which) -> getActivity().finish())
                            .setNegativeButton(
                                    android.R.string.cancel, null)
                            .create();
        }
    }
}
