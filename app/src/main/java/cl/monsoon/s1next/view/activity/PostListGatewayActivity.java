package cl.monsoon.s1next.view.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.Browser;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.Loader;
import android.text.TextUtils;

import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

import cl.monsoon.s1next.Api;
import cl.monsoon.s1next.App;
import cl.monsoon.s1next.R;
import cl.monsoon.s1next.data.api.model.Thread;
import cl.monsoon.s1next.data.pref.ThemeManager;
import cl.monsoon.s1next.util.IntentUtil;
import cl.monsoon.s1next.view.fragment.LoaderDialogFragment;
import cl.monsoon.s1next.widget.AsyncResult;
import cl.monsoon.s1next.widget.HttpRedirectLoader;

/**
 * An Activity to detect whether the thread link (URI) from Intent is valid.
 * Also show prompt if the thread corresponding to url do not exist.
 * <p>
 * This Activity is only used for Intent filter.
 */
public final class PostListGatewayActivity extends FragmentActivity {

    @Inject
    ThemeManager mThemeManager;

    public static final String ARG_COME_FROM_OTHER_APP = "come_from_other_app";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // default theme for this Activity is dark theme
        if (!App.getAppComponent(this).getThemeManager().isDarkTheme()) {
            setTheme(ThemeManager.TRANSLUCENT_THEME_LIGHT);
        }

        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            Uri uri = getIntent().getData();
            if (uri != null) {
                ThreadAnalysis threadAnalysis = parse(uri.toString());
                if (threadAnalysis == null) {
                    ErrorPromptDialogFragment.newInstance(
                            R.string.dialog_message_invalid_or_unsupported_link).show(
                            getSupportFragmentManager(), ErrorPromptDialogFragment.TAG);
                } else {
                    if (TextUtils.isEmpty(threadAnalysis.quotePostId)) {
                        startPostListActivity(threadAnalysis.threadId, threadAnalysis.jumpPage);
                        finish();
                    } else
                        QuotePostPageAnalysisDialogFragment.newInstance(threadAnalysis).show(
                                getSupportFragmentManager(), QuotePostPageAnalysisDialogFragment.TAG);
                }
            } else {
                throw new IllegalStateException("Uri can't be null.");
            }
        }
    }

    private static ThreadAnalysis parse(String url) {
        // example: http://bbs.saraba1st.com/2b/forum.php?mod=redirect&goto=findpost&pid=27217893&ptid=1074030
        Pattern pattern = Pattern.compile("ptid=(\\d+)");
        Matcher matcher = pattern.matcher(url);
        if (matcher.find()) {
            ThreadAnalysis.Builder builder = new ThreadAnalysis.Builder(matcher.group(1));

            matcher.usePattern(Pattern.compile("pid=(\\d+)"));
            if (matcher.find()) {
                builder.quotePostId(matcher.group(1));
            }

            return builder.build();
        }

        // example: http://bbs.saraba1st.com/2b/thread-1074030-1-1.html
        matcher.usePattern(Pattern.compile("thread-(\\d+)-(\\d+)"));
        if (matcher.find()) {
            return new ThreadAnalysis.Builder(matcher.group(1))
                    .jumpPage(Integer.parseInt(matcher.group(2)))
                    .build();
        }

        // example:
        // http://bbs.saraba1st.com/2b/forum.php?mod=viewthread&tid=1074030
        matcher.usePattern(Pattern.compile("tid=(\\d+)"));
        if (matcher.find()) {
            ThreadAnalysis.Builder builder = new ThreadAnalysis.Builder(matcher.group(1));

            // example: http://bbs.saraba1st.com/2b/forum.php?mod=viewthread&tid=1074030&page=7
            matcher.usePattern(Pattern.compile("page=(\\d+)"));
            if (matcher.find()) {
                builder.jumpPage(Integer.parseInt(matcher.group(1)));
            }

            return builder.build();
        }

        // example: http://bbs.saraba1st.com/2b/archiver/tid-1074030.html
        matcher.usePattern(Pattern.compile("tid-(\\d+)"));
        if (matcher.find()) {
            ThreadAnalysis.Builder builder = new ThreadAnalysis.Builder(matcher.group(1));

            // example: http://bbs.saraba1st.com/2b/archiver/tid-1074030.html?page=7
            matcher.usePattern(Pattern.compile("page=(\\d+)"));
            if (matcher.find()) {
                builder.jumpPage(Integer.parseInt(matcher.group(1)));
            }

            return builder.build();
        }

        return null;
    }

    private void startPostListActivity(String threadId, int jumpPage) {
        startPostListActivity(threadId, jumpPage, null);
    }

    private void startPostListActivity(String threadId, int jumpPage, String quotePostId) {
        Intent intent = new Intent(this, PostListActivity.class);

        Thread thread = new Thread();
        thread.setId(threadId);
        thread.setTitle(StringUtils.EMPTY);
        intent.putExtra(PostListActivity.ARG_THREAD, thread);
        intent.putExtra(PostListActivity.ARG_JUMP_PAGE, jumpPage);
        if (!TextUtils.isEmpty(quotePostId)) {
            intent.putExtra(PostListActivity.ARG_QUOTE_POST_ID, quotePostId);
        }
        // see android.text.style.URLSpan#onClick(View)
        intent.putExtra(ARG_COME_FROM_OTHER_APP, !getPackageName().equals(
                getIntent().getStringExtra(Browser.EXTRA_APPLICATION_ID)));

        startActivity(intent);
    }

    private static class ThreadAnalysis implements Parcelable {

        public static final Parcelable.Creator<ThreadAnalysis> CREATOR =
                new Parcelable.Creator<ThreadAnalysis>() {
                    @Override
                    public ThreadAnalysis createFromParcel(Parcel source) {
                        return new ThreadAnalysis(source);
                    }

                    @Override
                    public ThreadAnalysis[] newArray(int size) {
                        return new ThreadAnalysis[size];
                    }
                };

        private final String threadId;
        private final int jumpPage;
        private final String quotePostId;

        private ThreadAnalysis(Parcel source) {
            threadId = source.readString();
            jumpPage = source.readInt();
            quotePostId = source.readString();
        }

        private ThreadAnalysis(Builder builder) {
            this.threadId = builder.threadId;
            this.jumpPage = builder.jumpPage;
            this.quotePostId = builder.quotePostId;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(threadId);
            dest.writeInt(jumpPage);
            dest.writeString(quotePostId);
        }

        public static class Builder {

            private final String threadId;
            private int jumpPage = 1;
            private String quotePostId;

            public Builder(String threadId) {
                this.threadId = threadId;
            }

            public Builder jumpPage(int jumpPage) {
                this.jumpPage = jumpPage;
                return this;
            }

            public Builder quotePostId(String quotePostId) {
                this.quotePostId = quotePostId;
                return this;
            }

            public ThreadAnalysis build() {
                return new ThreadAnalysis(this);
            }
        }
    }

    /**
     * Gets the quote post's page in the thread.
     */
    public static class QuotePostPageAnalysisDialogFragment extends LoaderDialogFragment<HttpRedirectLoader.RedirectUrl> {

        private static final String TAG = QuotePostPageAnalysisDialogFragment.class.getName();

        private static final String ARG_THREAD_ANALYSIS = "thread_analysis";

        private ThreadAnalysis mThreadAnalysis;

        private boolean mShouldDismiss = true;

        public static QuotePostPageAnalysisDialogFragment newInstance(ThreadAnalysis threadAnalysis) {
            QuotePostPageAnalysisDialogFragment fragment = new QuotePostPageAnalysisDialogFragment();

            Bundle bundle = new Bundle();
            bundle.putParcelable(ARG_THREAD_ANALYSIS, threadAnalysis);
            fragment.setArguments(bundle);

            return fragment;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            mThreadAnalysis = getArguments().getParcelable(ARG_THREAD_ANALYSIS);
        }

        @Override
        protected CharSequence getProgressMessage() {
            return getString(R.string.dialog_message_processing);
        }

        @Override
        public void onDismiss(DialogInterface dialog) {
            super.onDismiss(dialog);

            if (mShouldDismiss) {
                // getActivity() = null when configuration changes (like orientation changes)
                if (getActivity() != null) {
                    getActivity().finish();
                }
            }
        }

        @Override
        public Loader<AsyncResult<HttpRedirectLoader.RedirectUrl>> onCreateLoader(@LoaderId int id, Bundle args) {
            return new HttpRedirectLoader(getActivity(), Api.getQuotePostRedirectUrl(
                    mThreadAnalysis.threadId, mThreadAnalysis.quotePostId));
        }

        @Override
        public void onLoadFinished(Loader<AsyncResult<HttpRedirectLoader.RedirectUrl>> loader, AsyncResult<HttpRedirectLoader.RedirectUrl> asyncResult) {
            if (asyncResult.exception != null) {
                mShouldDismiss = false;

                String exception;
                String exceptionPrefix = getString(asyncResult.getExceptionStringRes());
                String period = getString(R.string.period);
                // https://developer.android.com/design/style/writing.html#punctuation
                // we do not use a period after a single sentence in a toast
                // se we need add a period to the dialog message
                if (!exceptionPrefix.endsWith(period)) {
                    exception = exceptionPrefix;
                } else {
                    exception = exceptionPrefix + period;
                }
                new Handler().post(() ->
                        ErrorPromptDialogFragment.newInstance(exception).show(getFragmentManager(),
                                ErrorPromptDialogFragment.TAG));
            } else {
                int page = parseQuotePostPage(asyncResult.data.getUrl());
                if (page == -1) {
                    mShouldDismiss = false;
                    new Handler().post(() ->
                            ErrorPromptDialogFragment.newInstance(
                                    R.string.dialog_message_quote_not_found)
                                    .show(getFragmentManager(), ErrorPromptDialogFragment.TAG));
                } else {
                    ((PostListGatewayActivity) getActivity()).startPostListActivity(
                            mThreadAnalysis.threadId, page, mThreadAnalysis.quotePostId);
                }
                new Handler().post(QuotePostPageAnalysisDialogFragment.this::dismiss);
            }
        }

        private static int parseQuotePostPage(String url) {
            // example: http://bbs.saraba1st.com/2b/forum.php?mod=viewthread&tid=1074030&page=1#pid27217893
            Pattern pattern = Pattern.compile("page=(\\d+)");
            Matcher matcher = pattern.matcher(url);
            if (matcher.find()) {
                return Integer.parseInt(matcher.group(1));
            }
            return -1;
        }
    }

    public static class ErrorPromptDialogFragment extends DialogFragment {

        private static final String TAG = ErrorPromptDialogFragment.class.getName();

        private static final String ARG_MESSAGE = "message";

        public static ErrorPromptDialogFragment newInstance(@StringRes int message) {
            return newInstance(App.get().getString(message));
        }

        public static ErrorPromptDialogFragment newInstance(String message) {
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
