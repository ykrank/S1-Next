package me.ykrank.s1next.view.fragment;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;

import me.ykrank.s1next.App;
import me.ykrank.s1next.R;
import me.ykrank.s1next.data.api.S1Service;
import me.ykrank.s1next.data.api.model.ThreadType;
import me.ykrank.s1next.databinding.FragmentNewThreadBinding;
import me.ykrank.s1next.util.L;
import me.ykrank.s1next.util.RxJavaUtil;
import me.ykrank.s1next.view.adapter.ThreadTypeSpinnerAdapter;
import me.ykrank.s1next.view.dialog.NewThreadRequestDialogFragment;
import me.ykrank.s1next.view.dialog.ReplyRequestDialogFragment;
import me.ykrank.s1next.view.internal.NewThreadCacheModel;
import rx.Single;
import rx.Subscription;

/**
 * A Fragment shows {@link EditText} to let the user enter reply.
 */
public final class NewThreadFragment extends BasePostFragment {

    public static final String TAG = NewThreadFragment.class.getName();

    private static final String ARG_FORUM_ID = "forum_id";

    private static final String CACHE_KEY_PREFIX = "NewThread_%s";
    @Inject
    S1Service mS1Service;
    @Inject
    ObjectMapper objectMapper;
    private String cacheKey;
    private int mForumId;
    private Subscription mSubscription;

    private EditText titleEditText;
    private Spinner typeSpinner;

    private NewThreadCacheModel cacheModel;

    public static NewThreadFragment newInstance(int forumId) {
        NewThreadFragment fragment = new NewThreadFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_FORUM_ID, forumId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FragmentNewThreadBinding newThreadBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_new_thread, container, false);
        initCreateView(newThreadBinding.layoutPost);
        titleEditText = newThreadBinding.title;
        typeSpinner = newThreadBinding.spinner;
        return newThreadBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mForumId = getArguments().getInt(ARG_FORUM_ID);
        cacheKey = String.format(CACHE_KEY_PREFIX, mForumId);
        L.leaveMsg("NewThreadFragment##mForumId:" + mForumId);

        App.getPrefComponent(getContext()).inject(this);
        init();
    }

    @Override
    public void onDestroy() {
        RxJavaUtil.unsubscribeIfNotNull(mSubscription);
        super.onDestroy();
    }

    @Override
    protected boolean OnMenuSendClick() {
        ThreadType selectType = (ThreadType) typeSpinner.getSelectedItem();
        if (selectType == null) {
            showShortSnackbar(R.string.error_not_init);
            return true;
        }
        String typeId = selectType.getTypeId();
        //未选择类别
        if (typeId == null || "0".equals(typeId.trim())) {
            showShortSnackbar(R.string.error_no_type_id);
            return true;
        }

        String title = titleEditText.getText().toString();
        String message = mReplyView.getText().toString();
        if (!isTitleValid(title) || !isMessageValid(message)) {
            showShortSnackbar(R.string.error_no_title_or_message);
            return true;
        }

        NewThreadRequestDialogFragment.newInstance(mForumId, typeId, title, message)
                .show(getFragmentManager(), ReplyRequestDialogFragment.TAG);

        return true;
    }

    @Override
    public String getCacheKey() {
        return cacheKey;
    }

    @Override
    public boolean isContentEmpty() {
        return super.isContentEmpty() && (titleEditText == null || TextUtils.isEmpty(titleEditText.getText()));
    }

    @Nullable
    @Override
    public String buildCacheString() {
        NewThreadCacheModel model = new NewThreadCacheModel();
        model.setSelectPosition(typeSpinner.getSelectedItemPosition());
        model.setTitle(titleEditText.getText().toString());
        model.setMessage(mReplyView.getText().toString());
        try {
            return objectMapper.writeValueAsString(model);
        } catch (JsonProcessingException e) {
            L.e(e);
        }
        return super.buildCacheString();
    }

    @Override
    public Subscription resumeFromCache(Single<String> cache) {
        return cache.map(s -> {
            try {
                return objectMapper.readValue(s, NewThreadCacheModel.class);
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        }).compose(RxJavaUtil.iOSingleTransformer())
                .subscribe(model -> {
                    if (model != null) {
                        cacheModel = model;
                        titleEditText.setText(model.getTitle());
                        mReplyView.setText(model.getMessage());
                    }
                }, L::e);
    }

    private boolean isTitleValid(String string) {
        if (TextUtils.isEmpty(string) || string.trim().length() == 0) {
            return false;
        }
        return true;
    }

    private boolean isMessageValid(String string) {
        return isTitleValid(string);
    }

    private void init() {
        mSubscription = mS1Service.getNewThreadInfo(mForumId)
                .map(ThreadType::fromXmlString)
                .compose(RxJavaUtil.iOTransformer())
                .subscribe(types -> {
                    if (types == null || types.isEmpty()) {
                        showRetrySnackbar(getString(R.string.message_network_error), v -> init());
                    } else {
                        setSpinner(types);
                    }
                }, e -> {
                    L.e(e);
                    showRetrySnackbar(getString(R.string.message_network_error), v -> init());
                });
    }

    private void setSpinner(List<ThreadType> types) {
        ThreadTypeSpinnerAdapter spinnerAdapter = new ThreadTypeSpinnerAdapter(getContext(), types);
        typeSpinner.setAdapter(spinnerAdapter);
        if (types.size() > cacheModel.getSelectPosition()) {
            typeSpinner.setSelection(cacheModel.getSelectPosition());
        }
    }

}
