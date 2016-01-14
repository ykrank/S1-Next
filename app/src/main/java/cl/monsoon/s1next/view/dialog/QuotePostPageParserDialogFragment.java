package cl.monsoon.s1next.view.dialog;

import android.os.Bundle;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cl.monsoon.s1next.R;
import cl.monsoon.s1next.data.api.model.ThreadLink;
import cl.monsoon.s1next.util.ErrorUtil;
import cl.monsoon.s1next.view.activity.PostListActivity;
import rx.Observable;

/**
 * A {@link ProgressDialogFragment} parses quote post page for thread.
 */
public final class QuotePostPageParserDialogFragment extends ProgressDialogFragment<String> {

    public static final String TAG = QuotePostPageParserDialogFragment.class.getName();

    private static final String ARG_THREAD_LINK = "thread_link";

    private boolean mShouldFinishActivity = true;

    public static QuotePostPageParserDialogFragment newInstance(ThreadLink threadLink) {
        QuotePostPageParserDialogFragment fragment = new QuotePostPageParserDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(ARG_THREAD_LINK, threadLink);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mShouldFinishActivity) {
            getActivity().finish();
        }
    }

    @Override
    protected Observable<String> getSourceObservable() {
        ThreadLink threadLink = Preconditions.checkNotNull(getArguments().getParcelable(
                ARG_THREAD_LINK));
        return mS1Service.getQuotePostResponseBody(threadLink.getThreadId(),
                threadLink.getQuotePostId().get()).map(voidResponse ->
                voidResponse.raw().request().url().toString());
    }

    @Override
    protected void onNext(String url) {
        Optional<Integer> jumpPage = parseQuotePostPage(url);
        if (jumpPage.isPresent()) {
            ThreadLink threadLink = Preconditions.checkNotNull(getArguments().getParcelable(
                    ARG_THREAD_LINK));
            ThreadLink threadLinkWithJumpPage = new ThreadLink.Builder(threadLink.getThreadId())
                    .jumpPage(jumpPage.get())
                    .quotePostId(threadLink.getQuotePostId().get())
                    .build();
            PostListActivity.startPostListActivity(getActivity(), threadLinkWithJumpPage);
        } else {
            ThreadLinkInvalidPromptDialogFragment.newInstance(getContext(),
                    R.string.dialog_message_quote_not_found).show(getFragmentManager(),
                    ThreadLinkInvalidPromptDialogFragment.TAG);
            mShouldFinishActivity = false;
        }
    }

    @Override
    protected void onError(Throwable throwable) {
        ThreadLinkInvalidPromptDialogFragment.newInstance(getContext(), ErrorUtil.parse(throwable))
                .show(getFragmentManager(), ThreadLinkInvalidPromptDialogFragment.TAG);
        mShouldFinishActivity = false;
    }

    @Override
    protected CharSequence getProgressMessage() {
        return getString(R.string.dialog_message_processing);
    }

    /**
     * Parses redirect link in order to get quote post page.
     *
     * @param url The redirect link.
     */
    private Optional<Integer> parseQuotePostPage(String url) {
        // example: http://bbs.saraba1st.com/2b/forum.php?mod=viewthread&tid=1074030&page=1#pid27217893
        Pattern pattern = Pattern.compile("page=(\\d+)");
        Matcher matcher = pattern.matcher(url);
        if (matcher.find()) {
            return Optional.of(Integer.parseInt(matcher.group(1)));
        }
        return Optional.absent();
    }
}
