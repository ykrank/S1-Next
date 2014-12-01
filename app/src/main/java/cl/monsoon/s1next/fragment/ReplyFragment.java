package cl.monsoon.s1next.fragment;

import android.content.Loader;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.squareup.okhttp.RequestBody;

import cl.monsoon.s1next.Api;
import cl.monsoon.s1next.R;
import cl.monsoon.s1next.model.Result;
import cl.monsoon.s1next.model.mapper.ResultWrapper;
import cl.monsoon.s1next.singleton.User;
import cl.monsoon.s1next.util.ToastHelper;
import cl.monsoon.s1next.widget.AsyncResult;
import cl.monsoon.s1next.widget.HttpGetLoader;
import cl.monsoon.s1next.widget.HttpPostLoader;

/**
 * Send the reply via EditView.
 */
public final class ReplyFragment extends LoaderFragment implements View.OnClickListener {

    public static final String TAG = "reply_fragment";

    private final static String ARG_THREAD_TITLE = "thread_title";
    private static final String ARG_THREAD_ID = "thread_id";

    private static final String STATUS_REPLY_SUCCESS = "post_reply_succeed";

    private static final int ID_LOADER_GET_AUTHENTICITY_TOKEN = 0;
    private static final int ID_LOADER_POST_REPLY = 1;

    private CharSequence mThreadId;

    /**
     * The reply we need to send.
     */
    private EditText mReplyView;

    public static ReplyFragment newInstance(CharSequence title, String threadId) {
        ReplyFragment fragment = new ReplyFragment();

        Bundle args = new Bundle();
        args.putCharSequence(ARG_THREAD_TITLE, title);
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

        // show user's avatar
        Glide.with(this)
                .load(Api.getUrlAvatarMedium(User.getUid()))
                .error(R.drawable.ic_avatar_placeholder)
                .transform(new CenterCrop(Glide.get(getActivity()).getBitmapPool()))
                .into((ImageView) view.findViewById(R.id.avatar));

        String username = User.getName();
        if (TextUtils.isEmpty(username)) {
            throw new IllegalStateException("Username must not be null.");
        }

        // show username and post title
        ((TextView) view.findViewById(R.id.username)).setText(username);
        ((TextView) view.findViewById(R.id.title))
                .setText(getArguments().getCharSequence(ARG_THREAD_TITLE));

        mReplyView = (EditText) view.findViewById(R.id.comment_or_reply);

        View sendButton = view.findViewById(R.id.send);
        sendButton.setOnClickListener(this);

        // disable send button because the content of reply is empty
        sendButton.setEnabled(false);
        mReplyView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(s.toString())) {
                    sendButton.setEnabled(false);
                } else {
                    sendButton.setEnabled(true);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
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
        if (loaderId == ID_LOADER_GET_AUTHENTICITY_TOKEN) {
            throw new IllegalStateException("loaderId can't be ID_LOADER_GET_AUTHENTICITY_TOKEN.");
        }

        return Api.getReplyPostBuilder(mReplyView.getText().toString());
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
