package me.ykrank.s1next.view.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;

import me.ykrank.s1next.util.DeviceUtil;
import me.ykrank.s1next.view.dialog.ReplyRequestDialogFragment;

/**
 * A Fragment shows {@link EditText} to let the user enter reply.
 */
public final class ReplyFragment extends BasePostFragment {

    public static final String TAG = ReplyFragment.class.getName();

    private static final String ARG_THREAD_ID = "thread_id";
    private static final String ARG_QUOTE_POST_ID = "quote_post_id";

    private String mThreadId;
    private String mQuotePostId;

    public static ReplyFragment newInstance(String threadId, @Nullable String quotePostId) {
        ReplyFragment fragment = new ReplyFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_THREAD_ID, threadId);
        bundle.putString(ARG_QUOTE_POST_ID, quotePostId);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mThreadId = getArguments().getString(ARG_THREAD_ID);
        mQuotePostId = getArguments().getString(ARG_QUOTE_POST_ID);
    }

    @Override
    protected boolean OnMenuSendClick() {
        StringBuilder stringBuilder = new StringBuilder(mReplyView.getText());
        if (mGeneralPreferencesManager.isSignatureEnabled()) {
            stringBuilder.append("\n\n").append(DeviceUtil.getSignature(getContext()));
        }

        ReplyRequestDialogFragment.newInstance(mThreadId, mQuotePostId,
                stringBuilder.toString()).show(getFragmentManager(),
                ReplyRequestDialogFragment.TAG);

        return true;
    }
}
