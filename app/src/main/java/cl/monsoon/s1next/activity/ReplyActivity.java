package cl.monsoon.s1next.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import cl.monsoon.s1next.R;
import cl.monsoon.s1next.fragment.ReplyFragment;
import cl.monsoon.s1next.singleton.Config;

/**
 * An Activity to send a reply.
 */
public final class ReplyActivity extends ActionBarActivity {

    public final static String ARG_THREAD_TITLE = "thread_title";
    public final static String ARG_THREAD_ID = "thread_id";

    private ReplyFragment mReplyFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // default theme is TranslucentDarkTheme
        if (Config.getCurrentTheme() == Config.LIGHT_THEME) {
            setTheme(Config.TRANSLUCENT_LIGHT_THEME);
        } else {
            setTheme(Config.TRANSLUCENT_DARK_THEME);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply);

        CharSequence title = getIntent().getStringExtra(ARG_THREAD_TITLE);
        String url = getIntent().getStringExtra(ARG_THREAD_ID);

        Fragment fragment = getFragmentManager().findFragmentByTag(ReplyFragment.TAG);
        if (fragment == null) {
            mReplyFragment = ReplyFragment.newInstance(title, url);

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
                            .setMessage(R.string.dialog_progress_message_back)
                            .setPositiveButton(android.R.string.ok,
                                    (dialog, which) -> getActivity().finish())
                            .setNegativeButton(
                                    android.R.string.cancel, null)
                            .create();
        }
    }
}
