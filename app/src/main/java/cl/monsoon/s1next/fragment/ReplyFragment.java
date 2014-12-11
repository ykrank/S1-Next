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
import cl.monsoon.s1next.model.Result;
import cl.monsoon.s1next.model.mapper.ResultWrapper;
import cl.monsoon.s1next.singleton.Config;
import cl.monsoon.s1next.singleton.User;
import cl.monsoon.s1next.util.ToastHelper;
import cl.monsoon.s1next.widget.AsyncResult;
import cl.monsoon.s1next.widget.HttpGetLoader;
import cl.monsoon.s1next.widget.HttpPostLoader;

/**
 * Send the reply via EditView.
 */
public final class ReplyFragment extends LoaderFragment {

    public static final String TAG = "reply_fragment";

    private static final String ARG_THREAD_ID = "thread_id";

    private static final String STATUS_REPLY_SUCCESS = "post_reply_succeed";

    private CharSequence mThreadId;

    /**
     * The reply we need to send.
     */
    private EditText mReplyView;

    private MenuItem mMenuReplyPost;

    public static ReplyFragment newInstance(String threadId) {
        ReplyFragment fragment = new ReplyFragment();

        Bundle args = new Bundle();
        args.putString(ARG_THREAD_ID, threadId);
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

        mThreadId = getArguments().getString(ARG_THREAD_ID);

        mReplyView = (EditText) view.findViewById(R.id.comment_or_reply);
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
                // We need to get authenticity token (formhash) if we haven't.
                // Then posts the rely.
                // see cl.monsoon.s1next.Api#URL_REPLY_HELPER
                int loaderId;
                if (TextUtils.isEmpty(User.getAuthenticityToken())) {
                    loaderId = ID_LOADER_GET_AUTHENTICITY_TOKEN;
                } else {
                    loaderId = ID_LOADER_POST_REPLY;
                }
                startLoader(loaderId);

                return true;
        }

        return super.onOptionsItemSelected(item);
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
        } else {
            throw new IllegalStateException("loaderId must be ID_LOADER_POST_REPLY.");
        }
    }

    @Override
    public Loader<AsyncResult<ResultWrapper>> onCreateLoader(int id, Bundle args) {
        if (id == ID_LOADER_GET_AUTHENTICITY_TOKEN) {
            return
                    new HttpGetLoader<>(
                            getActivity(),
                            Api.URL_REPLY_HELPER,
                            ResultWrapper.class);
        } else if (id == ID_LOADER_POST_REPLY) {
            return
                    new HttpPostLoader<>(
                            getActivity(),
                            Api.getPostRely(mThreadId),
                            ResultWrapper.class,
                            getRequestBody(id));
        } else {
            throw new ClassCastException("Loader id can't be " + id + ".");
        }
    }

    @Override
    public void onLoadFinished(Loader<AsyncResult<ResultWrapper>> loader, AsyncResult<ResultWrapper> asyncResult) {
        if (asyncResult.exception != null) {
            AsyncResult.handleException(asyncResult.exception);
        } else {
            int id = loader.getId();
            if (id == ID_LOADER_GET_AUTHENTICITY_TOKEN) {
                if (TextUtils.isEmpty(User.getAuthenticityToken())) {
                    throw new IllegalStateException("Authenticity Token can't be empty.");
                }

                startLoader(ID_LOADER_POST_REPLY);

                return;
            } else if (id == ID_LOADER_POST_REPLY) {
                ResultWrapper wrapper = asyncResult.data;
                Result result = wrapper.getResult();

                ToastHelper.showByText(result.getValue());

                if (result.getStatus().equals(STATUS_REPLY_SUCCESS)) {
                    getActivity().finish();
                }
            } else {
                throw new ClassCastException("Loader id can't be " + id + ".");
            }
        }

        super.onLoadFinished(loader, asyncResult);
    }
}
