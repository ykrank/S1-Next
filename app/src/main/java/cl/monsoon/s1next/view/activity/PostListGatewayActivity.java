package cl.monsoon.s1next.view.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

import cl.monsoon.s1next.Api;
import cl.monsoon.s1next.App;
import cl.monsoon.s1next.R;
import cl.monsoon.s1next.data.api.model.ThreadLink;
import cl.monsoon.s1next.data.pref.ThemeManager;
import cl.monsoon.s1next.util.IntentUtil;
import cl.monsoon.s1next.view.dialog.ProgressDialogFragment;
import rx.Observable;
import rx.Subscriber;

/**
 * An Activity to detect whether the thread link (URI) from Intent is valid.
 * Also show prompt if the thread corresponding to url do not exist.
 * <p>
 * This Activity is only used for Intent filter.
 */
public final class PostListGatewayActivity extends FragmentActivity {

    @Inject
    ThemeManager mThemeManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // default theme for this Activity is dark theme
        if (!App.getAppComponent(this).getThemeManager().isDarkTheme()) {
            setTheme(ThemeManager.TRANSLUCENT_THEME_LIGHT);
        }

        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            Uri uri = getIntent().getData();
            Optional<ThreadLink> threadLink = ThreadLink.parse(uri.toString());
            if (threadLink.isPresent()) {
                ThreadLink threadLinkInstance = threadLink.get();
                if (threadLinkInstance.getQuotePostId().isPresent()) {
                    QuotePostPageParserDialogFragment.newInstance(threadLinkInstance).show(
                            getSupportFragmentManager(), QuotePostPageParserDialogFragment.TAG);
                } else {
                    PostListActivity.startPostListActivity(this, threadLinkInstance);
                    finish();
                }
            } else {
                ErrorPromptDialogFragment.newInstance(this,
                        R.string.dialog_message_invalid_or_unsupported_link).show(
                        getSupportFragmentManager(), ErrorPromptDialogFragment.TAG);
            }
        }
    }

    /**
     * Gets the quote post's page in the thread.
     */
    public static class QuotePostPageParserDialogFragment extends ProgressDialogFragment<String> {

        private static final String TAG = QuotePostPageParserDialogFragment.class.getName();

        private static final String ARG_THREAD_LINK = "thread_link";

        private static QuotePostPageParserDialogFragment newInstance(ThreadLink threadLink) {
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
                dismiss();
                PostListActivity.startPostListActivity(getActivity(), threadLinkWithJumpPage);
                getActivity().finish();
            } else {
                dismiss();
                ErrorPromptDialogFragment.newInstance(getActivity(),
                        R.string.dialog_message_quote_not_found).show(getFragmentManager(),
                        ErrorPromptDialogFragment.TAG);
            }
        }

        @Override
        protected void onError(Throwable throwable) {
            dismiss();
            ErrorPromptDialogFragment.newInstance(throwable.toString()).show(getFragmentManager(),
                    ErrorPromptDialogFragment.TAG);
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

    /**
     * A dialog shows error prompt if the thread link is invalid.
     * Clicks the negative button can let user open this thread link in browser.
     */
    public static class ErrorPromptDialogFragment extends DialogFragment {

        private static final String TAG = ErrorPromptDialogFragment.class.getName();

        private static final String ARG_MESSAGE = "message";

        private static ErrorPromptDialogFragment newInstance(Context context, @StringRes int message) {
            return newInstance(context.getString(message));
        }

        private static ErrorPromptDialogFragment newInstance(String message) {
            ErrorPromptDialogFragment fragment = new ErrorPromptDialogFragment();
            Bundle bundle = new Bundle();
            bundle.putString(ARG_MESSAGE, message);
            fragment.setArguments(bundle);

            return fragment;
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return new AlertDialog.Builder(getActivity())
                    .setMessage(getArguments().getString(ARG_MESSAGE))
                    .setPositiveButton(R.string.dialog_button_text_done,
                            (dialog, which) -> dismiss())
                    .setNegativeButton(R.string.dialog_button_text_use_a_different_app,
                            (dialog, which) ->
                                    IntentUtil.startViewIntentExcludeOurApp(getActivity(),
                                            getActivity().getIntent().getData()))
                    .create();
        }

        @Override
        public void onDismiss(DialogInterface dialog) {
            super.onDismiss(dialog);

            // getActivity() = null when configuration changes (like orientation changes)
            if (getActivity() != null) {
                getActivity().finish();
            }
        }
    }
}
