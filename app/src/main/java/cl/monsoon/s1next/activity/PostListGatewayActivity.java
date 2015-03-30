package cl.monsoon.s1next.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.Loader;
import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cl.monsoon.s1next.Api;
import cl.monsoon.s1next.MyApplication;
import cl.monsoon.s1next.R;
import cl.monsoon.s1next.fragment.LoaderDialogFragment;
import cl.monsoon.s1next.model.mapper.PostListWrapper;
import cl.monsoon.s1next.singleton.Config;
import cl.monsoon.s1next.util.IntentUtil;
import cl.monsoon.s1next.widget.AsyncResult;
import cl.monsoon.s1next.widget.HttpGetLoader;

/**
 * An Activity to detect whether the thread link (URI) from Intent is valid.
 * Also show prompt if the thread corresponding to url do not exist.
 * <p>
 * This Activity is only used for intent filter.
 */
public final class PostListGatewayActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // default theme for this Activity is dark theme
        if (!Config.isDarkTheme()) {
            setTheme(Config.TRANSLUCENT_THEME_LIGHT);
        }

        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            Uri uri = getIntent().getData();
            if (uri != null) {
                ThreadAnalysis threadAnalysis = parse(uri.toString());
                if (threadAnalysis == null) {
                    ErrorPromptDialog.newInstance(R.string.dialog_message_invalid_or_unsupported_link)
                            .show(getSupportFragmentManager(), ErrorPromptDialog.TAG);
                } else {
                    ThreadExistenceCheckDialogFragment.newInstance(threadAnalysis)
                            .show(getSupportFragmentManager(), ThreadExistenceCheckDialogFragment.TAG);
                }
            } else {
                throw new IllegalStateException("Uri can't be null.");
            }
        }
    }

    private static ThreadAnalysis parse(String url) {
        // example: http://bbs.saraba1st.com/2b/thread-1074030-1-1.html
        Pattern pattern = Pattern.compile("thread-(\\d+)-(\\d+).");
        Matcher matcher = pattern.matcher(url);
        if (matcher.find()) {
            return
                    new ThreadAnalysis.Builder(matcher.group(1))
                            .jumpPage(matcher.group(2))
                            .build();
        }

        // example:
        // http://bbs.saraba1st.com/2b/forum.php?mod=viewthread&tid=1074030
        // http://bbs.saraba1st.com/2b/forum.php?mod=redirect&tid=1074030&goto=lastpost#lastpost
        matcher.usePattern(Pattern.compile("tid=(\\d+)"));
        if (matcher.find()) {
            ThreadAnalysis.Builder builder = new ThreadAnalysis.Builder(matcher.group(1));

            // example: http://bbs.saraba1st.com/2b/forum.php?mod=viewthread&tid=1074030&page=7
            matcher.usePattern(Pattern.compile("page=(\\d+)"));
            if (matcher.find()) {
                builder.jumpPage(matcher.group(1));
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
                builder.jumpPage(matcher.group(1));
            }

            return builder.build();
        }

        return null;
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

        private final String mThreadId;
        private final int mJumpPage;

        private ThreadAnalysis(Parcel source) {
            mThreadId = source.readString();
            mJumpPage = source.readInt();
        }

        private ThreadAnalysis(Builder builder) {
            this.mThreadId = builder.mThreadId;
            if (TextUtils.isEmpty(builder.mJumpPage)) {
                // default page
                this.mJumpPage = 1;
            } else {
                this.mJumpPage = Integer.parseInt(builder.mJumpPage);
            }
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(mThreadId);
            dest.writeInt(mJumpPage);
        }

        public static class Builder {

            private final String mThreadId;
            private String mJumpPage;

            public Builder(String threadId) {
                this.mThreadId = threadId;
            }

            public Builder jumpPage(String jumpPage) {
                this.mJumpPage = jumpPage;
                return this;
            }

            public ThreadAnalysis build() {
                return new ThreadAnalysis(this);
            }
        }
    }

    public static class ThreadExistenceCheckDialogFragment extends LoaderDialogFragment<PostListWrapper> {

        private static final String TAG = ThreadExistenceCheckDialogFragment.class.getSimpleName();

        private static final String ARG_THREAD_ANALYSIS = "thread_analysis";

        private boolean mShouldDismiss = true;

        public static ThreadExistenceCheckDialogFragment newInstance(ThreadAnalysis threadAnalysis) {
            ThreadExistenceCheckDialogFragment fragment = new ThreadExistenceCheckDialogFragment();

            Bundle bundle = new Bundle();
            bundle.putParcelable(ARG_THREAD_ANALYSIS, threadAnalysis);
            fragment.setArguments(bundle);

            return fragment;
        }

        @Override
        protected CharSequence getProgressMessage() {
            return getString(R.string.dialog_message_loading);
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
        public Loader<AsyncResult<PostListWrapper>> onCreateLoader(int id, Bundle args) {
            ThreadAnalysis threadAnalysis = getArguments().getParcelable(ARG_THREAD_ANALYSIS);
            return
                    new HttpGetLoader<>(
                            getActivity(),
                            Api.getPostListUrl(threadAnalysis.mThreadId, threadAnalysis.mJumpPage),
                            PostListWrapper.class);
        }

        @Override
        public void onLoadFinished(Loader<AsyncResult<PostListWrapper>> loader, AsyncResult<PostListWrapper> asyncResult) {
            if (asyncResult.exception != null) {
                mShouldDismiss = false;

                String exceptionWithPeriod;
                String exception = getString(asyncResult.getExceptionString());
                String period = getString(R.string.period);
                // https://developer.android.com/design/style/writing.html#punctuation
                // we do not use a period after a single sentence in a toast
                // se we need add a period
                if (!exception.endsWith(period)) {
                    exceptionWithPeriod = exception;
                } else {
                    exceptionWithPeriod = exception + period;
                }

                new Handler().post(() ->
                        ErrorPromptDialog.newInstance(exceptionWithPeriod)
                                .show(getFragmentManager(), ErrorPromptDialog.TAG));

            } else {
                cl.monsoon.s1next.model.Thread thread = asyncResult.data.unwrap().getInfo();

                if (TextUtils.isEmpty(thread.getId()) || TextUtils.isEmpty(thread.getTitle())) {
                    mShouldDismiss = false;
                    new Handler().post(() ->
                            ErrorPromptDialog.newInstance(R.string.dialog_message_thread_not_found)
                                    .show(getFragmentManager(), ErrorPromptDialog.TAG));
                } else {
                    Intent intent = new Intent(getActivity(), PostListActivity.class);
                    intent.putExtra(PostListActivity.ARG_THREAD, thread);
                    intent.putExtra(
                            IntentUtil.ARG_COME_FROM_OUR_APP,
                            IntentUtil.isComeFromOurApp(getActivity().getIntent(), getActivity()));
                    startActivity(intent);
                }
            }

            new Handler().post(ThreadExistenceCheckDialogFragment.this::dismiss);
        }
    }

    public static class ErrorPromptDialog extends DialogFragment {

        private static final String TAG = ErrorPromptDialog.class.getSimpleName();

        private static final String ARG_MESSAGE = "message";

        public static ErrorPromptDialog newInstance(@StringRes int message) {
            ErrorPromptDialog fragment = new ErrorPromptDialog();

            Bundle bundle = new Bundle();
            bundle.putString(ARG_MESSAGE, MyApplication.getContext().getString(message));
            fragment.setArguments(bundle);

            return fragment;
        }

        public static ErrorPromptDialog newInstance(String message) {
            ErrorPromptDialog fragment = new ErrorPromptDialog();

            Bundle bundle = new Bundle();
            bundle.putString(ARG_MESSAGE, message);
            fragment.setArguments(bundle);

            return fragment;
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            return
                    new AlertDialog.Builder(getActivity())
                            .setMessage(getArguments().getString(ARG_MESSAGE))
                            .setPositiveButton(android.R.string.ok,
                                    (dialog, which) -> {
                                        dismiss();
                                    })
                            .setNegativeButton(R.string.dialog_button_use_a_different_app,
                                    (dialog, which) -> {
                                        IntentUtil.startViewIntentExcludeOurApp(
                                                getActivity(), getActivity().getIntent().getData());
                                    })
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
