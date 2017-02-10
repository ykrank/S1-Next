package me.ykrank.s1next.view.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;

import org.apache.commons.lang3.StringUtils;

import me.ykrank.s1next.R;
import me.ykrank.s1next.util.L;
import me.ykrank.s1next.view.fragment.ReplyFragment;

/**
 * An Activity which used to send a reply.
 */
public final class ReplyActivity extends BaseActivity {

    private static final String ARG_THREAD_ID = "thread_id";
    private static final String ARG_THREAD_TITLE = "thread_title";

    private static final String ARG_QUOTE_POST_ID = "quote_post_id";
    private static final String ARG_QUOTE_POST_COUNT = "quote_post_count";

    private ReplyFragment mReplyFragment;

    public static void startReplyActivityForResultMessage(Activity activity, String threadId, @Nullable String threadTitle,
                                                          @Nullable String quotePostId, @Nullable String quotePostCount) {
        Intent intent = new Intent(activity, ReplyActivity.class);
        intent.putExtra(ARG_THREAD_ID, threadId);
        intent.putExtra(ARG_THREAD_TITLE, threadTitle);

        intent.putExtra(ARG_QUOTE_POST_ID, quotePostId);
        intent.putExtra(ARG_QUOTE_POST_COUNT, quotePostCount);

        BaseActivity.startActivityForResultMessage(activity, intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_without_drawer_and_scrolling_effect);

        setupNavCrossIcon();

        Intent intent = getIntent();
        String threadId = intent.getStringExtra(ARG_THREAD_ID);
        String quotePostId = intent.getStringExtra(ARG_QUOTE_POST_ID);
        L.leaveMsg("ReplyActivity##threadId:" + threadId + ",quotePostId:" + quotePostId);

        String titlePrefix = TextUtils.isEmpty(quotePostId)
                ? getString(R.string.reply_activity_title_prefix)
                : getString(R.string.reply_activity_quote_title_prefix,
                intent.getStringExtra(ARG_QUOTE_POST_COUNT));
        setTitle(titlePrefix + StringUtils.defaultString(intent.getStringExtra(ARG_THREAD_TITLE),
                StringUtils.EMPTY));

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentByTag(ReplyFragment.TAG);
        if (fragment == null) {
            mReplyFragment = ReplyFragment.newInstance(threadId,
                    quotePostId);
            fragmentManager.beginTransaction().add(R.id.frame_layout, mReplyFragment,
                    ReplyFragment.TAG).commit();
        } else {
            mReplyFragment = (ReplyFragment) fragment;
        }
    }

    /**
     * Show {@link android.app.AlertDialog} when reply content is not empty.
     */
    @Override
    public void onBackPressed() {
        if (mReplyFragment.isEmoticonKeyboardShowing()) {
            mReplyFragment.hideEmoticonKeyboard();
        } else {
            super.onBackPressed();
        }
    }
}
