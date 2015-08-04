package cl.monsoon.s1next.view.dialog;

import android.os.Bundle;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cl.monsoon.s1next.App;
import cl.monsoon.s1next.R;
import cl.monsoon.s1next.data.api.Api;
import cl.monsoon.s1next.data.api.model.ThreadLink;
import cl.monsoon.s1next.view.activity.PostListActivity;
import rx.Observable;
import rx.Subscriber;

/**
 * A {@link ProgressDialogFragment} parses quote post page for thread.
 */
public final class QuotePostPageParserDialogFragment extends ProgressDialogFragment<String> {

    public static final String TAG = QuotePostPageParserDialogFragment.class.getName();

    private static final String ARG_THREAD_LINK = "thread_link";

    public static QuotePostPageParserDialogFragment newInstance(ThreadLink threadLink) {
        QuotePostPageParserDialogFragment fragment = new QuotePostPageParserDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(ARG_THREAD_LINK, threadLink);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    protected Observable<String> getSourceObservable() {
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                ThreadLink threadLink = getArguments().getParcelable(ARG_THREAD_LINK);
                // get the redirect URL for quote post link.
                Request request = new Request.Builder()
                        .url(Api.getQuotePostRedirectUrl(threadLink))
                        .build();
                Call call = App.getAppComponent(getActivity()).getOkHttpClient().newCall(request);
                try {
                    Response response = call.execute();
                    response.body().close();
                    subscriber.onNext(response.request().urlString());
                    subscriber.onCompleted();
                } catch (IOException e) {
                    subscriber.onError(e);
                } finally {
                    call.cancel();
                }
            }
        });
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
            dismissAllowingStateLoss();
            PostListActivity.startPostListActivity(getActivity(), threadLinkWithJumpPage);
            getActivity().finish();
        } else {
            dismissAllowingStateLoss();
            ThreadLinkInvalidPromptDialogFragment.newInstance(getActivity(),
                    R.string.dialog_message_quote_not_found).show(getFragmentManager(),
                    ThreadLinkInvalidPromptDialogFragment.TAG);
        }
    }

    @Override
    protected void onError(Throwable throwable) {
        dismissAllowingStateLoss();
        ThreadLinkInvalidPromptDialogFragment.newInstance(throwable.toString()).show(getFragmentManager(),
                ThreadLinkInvalidPromptDialogFragment.TAG);
    }

    @Override
    protected void finallyDo() {

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
