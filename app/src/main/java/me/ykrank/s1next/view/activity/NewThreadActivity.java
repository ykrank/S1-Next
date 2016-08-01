package me.ykrank.s1next.view.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.MenuItem;

import me.ykrank.s1next.R;
import me.ykrank.s1next.view.dialog.DiscardEditPromptDialogFragment;
import me.ykrank.s1next.view.fragment.NewThreadFragment;

/**
 * An Activity which used to send a reply.
 */
public final class NewThreadActivity extends BaseActivity {

    private static final String ARG_FORUM_ID = "forum_id";

    private NewThreadFragment mNewThreadFragment;

    public static void startNewThreadActivityForResultMessage(Activity activity, int forumId) {
        Intent intent = new Intent(activity, NewThreadActivity.class);
        intent.putExtra(ARG_FORUM_ID, forumId);

        BaseActivity.startActivityForResultMessage(activity, intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_without_drawer_and_scrolling_effect);

        setupNavCrossIcon();

        Intent intent = getIntent();
        setTitle(R.string.title_new_thread);

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentByTag(NewThreadFragment.TAG);
        if (fragment == null) {
            mNewThreadFragment = NewThreadFragment.newInstance(intent.getIntExtra(ARG_FORUM_ID, 75));
            fragmentManager.beginTransaction().add(R.id.frame_layout, mNewThreadFragment,
                    NewThreadFragment.TAG).commit();
        } else {
            mNewThreadFragment = (NewThreadFragment) fragment;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (mNewThreadFragment.isContentEmpty()) {
                    finish();
                } else {
                    discardDialog();
                }

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Show {@link android.app.AlertDialog} when reply content is not empty.
     */
    @Override
    public void onBackPressed() {
        if (mNewThreadFragment.isEmoticonKeyboardShowing()) {
            mNewThreadFragment.hideEmoticonKeyboard();
        } else if (mNewThreadFragment.isContentEmpty()) {
            super.onBackPressed();
        } else {
            discardDialog();
        }
    }

    private void discardDialog() {
        DiscardEditPromptDialogFragment.newInstance(getString(R.string.dialog_message_new_thread_discard_prompt))
                .show(getSupportFragmentManager(), DiscardEditPromptDialogFragment.TAG);
    }
}
