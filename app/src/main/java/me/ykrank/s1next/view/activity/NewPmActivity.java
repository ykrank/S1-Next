package me.ykrank.s1next.view.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;

import me.ykrank.s1next.R;
import me.ykrank.s1next.view.fragment.NewPmFragment;

/**
 * An Activity which used to send a reply.
 */
public final class NewPmActivity extends BaseActivity {

    private static final String ARG_TO_UID = "arg_to_uid";
    private static final String ARG_TO_USERNAME = "to_user_name";

    private NewPmFragment newPmFragment;

    public static void startNewPmActivityForResultMessage(Activity activity, @NonNull String toUid, @NonNull String toUsername) {
        Intent intent = new Intent(activity, NewPmActivity.class);
        intent.putExtra(ARG_TO_UID, toUid);
        intent.putExtra(ARG_TO_USERNAME, toUsername);

        BaseActivity.startActivityForResultMessage(activity, intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_without_drawer_and_scrolling_effect);

        setupNavCrossIcon();

        Intent intent = getIntent();
        String toUid = intent.getStringExtra(ARG_TO_UID);
        String toUsername = intent.getStringExtra(ARG_TO_USERNAME);
        if (TextUtils.isEmpty(toUid)) {
            showShortSnackbar(R.string.message_api_error);
            return;
        }
        setTitle(getString(R.string.title_new_pm) + "-" + toUsername);

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentByTag(NewPmFragment.TAG);
        if (fragment == null) {
            newPmFragment = NewPmFragment.newInstance(toUid);
            fragmentManager.beginTransaction().add(R.id.frame_layout, newPmFragment,
                    NewPmFragment.TAG).commit();
        } else {
            newPmFragment = (NewPmFragment) fragment;
        }
    }

    /**
     * Show {@link android.app.AlertDialog} when reply content is not empty.
     */
    @Override
    public void onBackPressed() {
        if (newPmFragment.isEmoticonKeyboardShowing()) {
            newPmFragment.hideEmoticonKeyboard();
        } else {
            super.onBackPressed();
        }
    }
}
