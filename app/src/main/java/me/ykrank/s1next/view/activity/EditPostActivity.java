package me.ykrank.s1next.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import me.ykrank.s1next.R;
import me.ykrank.s1next.data.api.model.Post;
import me.ykrank.s1next.data.api.model.Thread;
import me.ykrank.s1next.view.fragment.EditPostFragment;

/**
 * An Activity to new a thread.
 */
public final class EditPostActivity extends BaseActivity {

    private static final String ARG_THREAD = "thread";
    private static final String ARG_POST = "post";

    private EditPostFragment mFragment;

    public static void startActivityForResultMessage(Fragment fragment, int requestCode, Thread thread, Post post) {
        Intent intent = new Intent(fragment.getContext(), EditPostActivity.class);
        intent.putExtra(ARG_THREAD, thread);
        intent.putExtra(ARG_POST, post);
        fragment.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_without_drawer_and_scrolling_effect);

        setupNavCrossIcon();

        Intent intent = getIntent();
        Thread mThread = intent.getParcelableExtra(ARG_THREAD);
        Post mPost = intent.getParcelableExtra(ARG_POST);
        setTitle(R.string.title_new_thread);

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentByTag(EditPostFragment.Companion.getTAG());
        if (fragment == null) {
            mFragment = EditPostFragment.Companion.newInstance(mThread, mPost);
            fragmentManager.beginTransaction().add(R.id.frame_layout, mFragment,
                    EditPostFragment.Companion.getTAG()).commit();
        } else {
            mFragment = (EditPostFragment) fragment;
        }
    }

    /**
     * Show {@link android.app.AlertDialog} when reply content is not empty.
     */
    @Override
    public void onBackPressed() {
        if (mFragment.isEmoticonKeyboardShowing()) {
            mFragment.hideEmoticonKeyboard();
        } else {
            super.onBackPressed();
        }
    }
}
