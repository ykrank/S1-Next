package cl.monsoon.s1next.fragment;

import android.content.Loader;
import android.os.Bundle;
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

import com.squareup.okhttp.RequestBody;

import cl.monsoon.s1next.Api;
import cl.monsoon.s1next.R;
import cl.monsoon.s1next.model.Quote;
import cl.monsoon.s1next.model.Result;
import cl.monsoon.s1next.model.mapper.ResultWrapper;
import cl.monsoon.s1next.singleton.Config;
import cl.monsoon.s1next.singleton.User;
import cl.monsoon.s1next.util.ObjectUtil;
import cl.monsoon.s1next.util.ToastHelper;
import cl.monsoon.s1next.widget.AsyncResult;
import cl.monsoon.s1next.widget.HttpGetLoader;
import cl.monsoon.s1next.widget.HttpPostLoader;

/**
 * Send the reply via EditView.
 */
public final class ReplyFragment extends LoaderFragment {

    public static final String TAG = "reply_fragment";

    /**
     * The serialization (saved instance state) Bundle key representing the quote POJO.
     */
    private static final String STATE_QUOTE = "quote";

    private static final String ARG_THREAD_ID = "thread_id";
    private static final String ARG_QUOTE_COUNT = "quote_count";

    private static final String STATUS_REPLY_SUCCESS = "post_reply_succeed";

    private CharSequence mThreadId;
    private CharSequence mQuoteCount;

    private Quote mQuote;

    /**
     * The reply we need to send.
     */
    private EditText mReplyView;

    private MenuItem mMenuReplyPost;

    public static ReplyFragment newInstance(CharSequence threadId, CharSequence quoteCount) {
        ReplyFragment fragment = new ReplyFragment();

        Bundle args = new Bundle();
        args.putCharSequence(ARG_THREAD_ID, threadId);
        args.putCharSequence(ARG_QUOTE_COUNT, quoteCount);
        fragment.setArguments(args);

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
        mQuoteCount = getArguments().getCharSequence(ARG_QUOTE_COUNT);

        mReplyView = (EditText) view.findViewById(R.id.reply);
        Config.updateTextSize(mReplyView);
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
                showProgressDialog();

                int loaderId;
                final boolean hasAuthenticityToken =
                        !TextUtils.isEmpty(User.getAuthenticityToken());
                if (hasAuthenticityToken) {
                    if (TextUtils.isEmpty(mQuoteCount)) {
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
                startLoader(loaderId);

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable(STATE_QUOTE, mQuote);
    }

    public boolean isReplyEmpty() {
        return mReplyView == null || TextUtils.isEmpty(mReplyView.getText().toString());
    }

    @Override
    public CharSequence getProgressMessage() {
        return getText(R.string.dialog_progress_title_reply);
    }

    @Override
    RequestBody getRequestBody(int loaderId) {
        if (loaderId == ID_LOADER_POST_REPLY) {
            return Api.getReplyPostBuilder(mReplyView.getText().toString());
        } else if (loaderId == ID_LOADER_POST_QUOTE) {
            return Api.getQuotePostBuilder(mQuote, mReplyView.getText().toString());
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
                            Api.getQuoteHelper(mThreadId, mQuoteCount),
                            Quote.class);
        } else if (id == ID_LOADER_POST_REPLY || id == ID_LOADER_POST_QUOTE) {
            return
                    new HttpPostLoader<>(
                            getActivity(),
                            Api.getPostRely(mThreadId),
                            ResultWrapper.class,
                            getRequestBody(id));
        } else {
            throw new ClassCastException("Loader ID can't be " + id + ".");
        }
    }

    @Override
    public void onLoadFinished(Loader loader, Object data) {
        AsyncResult asyncResult = ObjectUtil.cast(data, AsyncResult.class);
        if (asyncResult.exception != null) {
            AsyncResult.handleException(asyncResult.exception);
        } else {
            int id = loader.getId();
            if (id == ID_LOADER_GET_AUTHENTICITY_TOKEN) {
                if (TextUtils.isEmpty(mQuoteCount)) {
                    startLoader(ID_LOADER_POST_REPLY);
                } else {
                    startLoader(ID_LOADER_GET_QUOTE_EXTRA_INFO);
                }

                return;
            } else if (id == ID_LOADER_GET_QUOTE_EXTRA_INFO) {
                mQuote = ObjectUtil.cast(asyncResult.data, Quote.class);

                startLoader(ID_LOADER_POST_QUOTE);

                return;
            } else if (id == ID_LOADER_POST_REPLY || id == ID_LOADER_POST_QUOTE) {
                ResultWrapper wrapper = ObjectUtil.cast(asyncResult.data, ResultWrapper.class);
                Result result = wrapper.getResult();

                ToastHelper.showByText(result.getValue());

                if (result.getStatus().equals(STATUS_REPLY_SUCCESS)) {
                    getActivity().finish();
                }
            } else {
                throw new IllegalStateException("Loader ID can't be " + id + ".");
            }
        }

        dismissProgressDialog();
    }

    @Override
    public void onLoaderReset(Loader loader) {

    }
}
