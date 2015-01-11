package cl.monsoon.s1next.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.Loader;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.okhttp.RequestBody;

import cl.monsoon.s1next.Api;
import cl.monsoon.s1next.R;
import cl.monsoon.s1next.model.Quote;
import cl.monsoon.s1next.model.Result;
import cl.monsoon.s1next.model.mapper.ResultWrapper;
import cl.monsoon.s1next.singleton.User;
import cl.monsoon.s1next.util.ObjectUtil;
import cl.monsoon.s1next.util.ToastUtil;
import cl.monsoon.s1next.util.TextViewHelper;
import cl.monsoon.s1next.widget.AsyncResult;
import cl.monsoon.s1next.widget.HttpGetLoader;
import cl.monsoon.s1next.widget.HttpPostLoader;

/**
 * Send the reply via EditView.
 */
public final class ReplyFragment extends Fragment {

    public static final String TAG = "reply_fragment";

    /**
     * The serialization (saved instance state) Bundle key representing the quote POJO.
     */
    private static final String STATE_QUOTE = "quote";

    private static final String ARG_THREAD_ID = "thread_id";
    private static final String ARG_QUOTE_POST_ID = "quote_post_id";

    private static final String STATUS_REPLY_SUCCESS = "post_reply_succeed";

    private CharSequence mThreadId;
    private CharSequence mQuotePostId;

    private Quote mQuote;

    /**
     * The reply we need to send.
     */
    private EditText mReplyView;

    private MenuItem mMenuReplyPost;

    public static ReplyFragment newInstance(CharSequence threadId, CharSequence quotePostId) {
        ReplyFragment fragment = new ReplyFragment();

        Bundle bundle = new Bundle();
        bundle.putCharSequence(ARG_THREAD_ID, threadId);
        bundle.putCharSequence(ARG_QUOTE_POST_ID, quotePostId);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_reply, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mThreadId = getArguments().getCharSequence(ARG_THREAD_ID);
        mQuotePostId = getArguments().getCharSequence(ARG_QUOTE_POST_ID);

        mReplyView = (EditText) view.findViewById(R.id.reply);
        TextViewHelper.updateTextSize(new TextView[]{mReplyView});
        TextViewHelper.updateTextColorWhenS1Theme(new TextView[]{mReplyView});
        mReplyView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (mMenuReplyPost == null) {
                    return;
                }

                // disable send button because the content of reply is empty
                mMenuReplyPost.setEnabled(!TextUtils.isEmpty(s.toString()));
            }
        });

        if (savedInstanceState != null) {
            mQuote = savedInstanceState.getParcelable(STATE_QUOTE);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_reply, menu);

        mMenuReplyPost = menu.findItem(R.id.menu_reply_post);
        mMenuReplyPost.setEnabled(!TextUtils.isEmpty(mReplyView.getText().toString()));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_reply_post:
                ReplyLoaderDialogFragment.newInstance(mThreadId, mQuotePostId, mQuote, mReplyView.getText())
                        .show(getChildFragmentManager(), ReplyLoaderDialogFragment.TAG);

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable(STATE_QUOTE, mQuote);
    }

    public boolean isReplyEmpty() {
        return mReplyView == null || TextUtils.isEmpty(mReplyView.getText().toString());
    }

    public static class ReplyLoaderDialogFragment extends LoaderDialogFragment {

        private static final String TAG = "reply_loader_dialog";

        private static final String ARG_QUOTE = "quote";
        private static final String ARG_REPLY = "reply";

        private Quote mQuote;

        public static ReplyLoaderDialogFragment newInstance(CharSequence threadId, CharSequence quotePostId, Quote quote, CharSequence reply) {
            ReplyLoaderDialogFragment fragment = new ReplyLoaderDialogFragment();

            Bundle bundle = new Bundle();
            bundle.putCharSequence(ARG_THREAD_ID, threadId);
            bundle.putCharSequence(ARG_QUOTE_POST_ID, quotePostId);
            bundle.putParcelable(ARG_QUOTE, quote);
            bundle.putCharSequence(ARG_REPLY, reply);
            fragment.setArguments(bundle);

            return fragment;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            if (savedInstanceState == null) {
                mQuote = getArguments().getParcelable(ARG_QUOTE);
            } else {
                mQuote = savedInstanceState.getParcelable(STATE_QUOTE);
            }
        }

        @Override
        public void onSaveInstanceState(@NonNull Bundle outState) {
            super.onSaveInstanceState(outState);

            outState.putParcelable(STATE_QUOTE, mQuote);
        }

        @Override
        protected CharSequence getProgressMessage() {
            return getText(R.string.dialog_progress_message_reply);
        }

        @Override
        protected int getStartLoaderId() {
            int loaderId;
            final boolean hasAuthenticityToken =
                    !TextUtils.isEmpty(User.getAuthenticityToken());
            if (hasAuthenticityToken) {
                if (TextUtils.isEmpty(getArguments().getCharSequence(ARG_QUOTE_POST_ID))) {
                    loaderId = ID_LOADER_POST_REPLY;
                } else {
                    if (mQuote == null) {
                        // We need to get extra information for quote.
                        // see cl.monsoon.s1next.Api#URL_QUOTE_HELPER
                        loaderId = ID_LOADER_GET_QUOTE_EXTRA_INFO;
                    } else {
                        loaderId = ID_LOADER_POST_QUOTE;
                    }
                }
            } else {
                // We need to get authenticity token (formhash) if we haven't.
                // Then posts the rely.
                // see cl.monsoon.s1next.Api#URL_AUTHENTICITY_TOKEN_HELPER
                loaderId = ID_LOADER_GET_AUTHENTICITY_TOKEN;
            }

            return loaderId;
        }

        @Override
        protected RequestBody getRequestBody(int loaderId) {
            if (loaderId == ID_LOADER_POST_REPLY) {
                return
                        Api.getReplyPostBuilder(
                                getArguments().getCharSequence(ARG_REPLY).toString());
            } else if (loaderId == ID_LOADER_POST_QUOTE) {
                return
                        Api.getQuotePostBuilder
                                (mQuote, getArguments().getCharSequence(ARG_REPLY).toString());
            }

            return super.getRequestBody(loaderId);
        }

        @Override
        public Loader onCreateLoader(int id, Bundle args) {
            if (id == ID_LOADER_GET_AUTHENTICITY_TOKEN) {
                return
                        new HttpGetLoader<>(
                                getActivity(),
                                Api.URL_AUTHENTICITY_TOKEN_HELPER,
                                ResultWrapper.class);
            } else if (id == ID_LOADER_GET_QUOTE_EXTRA_INFO) {
                return
                        new HttpGetLoader<>(
                                getActivity(),
                                Api.getQuoteHelper(
                                        getArguments().getCharSequence(ARG_THREAD_ID),
                                        getArguments().getCharSequence(ARG_QUOTE_POST_ID)),
                                Quote.class);
            } else if (id == ID_LOADER_POST_REPLY || id == ID_LOADER_POST_QUOTE) {
                return
                        new HttpPostLoader<>(
                                getActivity(),
                                Api.getPostRely(getArguments().getCharSequence(ARG_THREAD_ID)),
                                ResultWrapper.class,
                                getRequestBody(id));
            }

            return super.onCreateLoader(id, args);
        }

        @Override
        @SuppressWarnings("unchecked")
        public void onLoadFinished(Loader loader, Object data) {
            AsyncResult asyncResult = ObjectUtil.cast(data, AsyncResult.class);
            if (asyncResult.exception != null) {
                AsyncResult.handleException(asyncResult.exception);
            } else {
                int id = loader.getId();
                if (id == ID_LOADER_GET_AUTHENTICITY_TOKEN) {
                    if (TextUtils.isEmpty(getArguments().getCharSequence(ARG_QUOTE_POST_ID))) {
                        getLoaderManager().initLoader(ID_LOADER_POST_REPLY, null, this);
                    } else {
                        getLoaderManager().initLoader(ID_LOADER_GET_QUOTE_EXTRA_INFO, null, this);
                    }

                    return;
                } else if (id == ID_LOADER_GET_QUOTE_EXTRA_INFO) {
                    mQuote = ObjectUtil.cast(asyncResult.data, Quote.class);
                    ObjectUtil.cast(getParentFragment(), ReplyFragment.class).mQuote = mQuote;

                    getLoaderManager().initLoader(ID_LOADER_POST_QUOTE, null, this);

                    return;
                } else if (id == ID_LOADER_POST_REPLY || id == ID_LOADER_POST_QUOTE) {
                    ResultWrapper wrapper = ObjectUtil.cast(asyncResult.data, ResultWrapper.class);
                    Result result = wrapper.getResult();

                    ToastUtil.showByText(result.getMessage(), Toast.LENGTH_LONG);

                    if (result.getStatus().equals(STATUS_REPLY_SUCCESS)) {
                        getActivity().finish();
                    }
                } else {
                    super.onLoadFinished(loader, asyncResult);
                }
            }

            new Handler().post(this::dismiss);
        }

        @Override
        public void onLoaderReset(Loader loader) {

        }
    }
}
