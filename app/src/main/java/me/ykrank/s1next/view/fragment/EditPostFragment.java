package me.ykrank.s1next.view.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.disposables.Disposable;
import me.ykrank.s1next.App;
import me.ykrank.s1next.R;
import me.ykrank.s1next.data.api.S1Service;
import me.ykrank.s1next.data.api.model.Post;
import me.ykrank.s1next.data.api.model.Thread;
import me.ykrank.s1next.data.api.model.ThreadType;
import me.ykrank.s1next.databinding.FragmentEditPostBinding;
import me.ykrank.s1next.util.ErrorUtil;
import me.ykrank.s1next.util.L;
import me.ykrank.s1next.util.RxJavaUtil;
import me.ykrank.s1next.view.adapter.SimpleSpinnerAdapter;
import me.ykrank.s1next.view.dialog.EditPostRequestDialogFragment;
import me.ykrank.s1next.widget.track.event.page.PageEndEvent;
import me.ykrank.s1next.widget.track.event.page.PageStartEvent;

/**
 * A Fragment shows {@link EditText} to let the user enter reply.
 */
public final class EditPostFragment extends BasePostFragment {

    public static final String TAG = EditPostFragment.class.getName();

    private static final String ARG_THREAD = "thread";
    private static final String ARG_POST = "post";

    @Inject
    S1Service mS1Service;

    private Thread mThread;
    private Post mPost;
    private boolean isHost;

    private Disposable mDisposable;

    private FragmentEditPostBinding binding;

    public static EditPostFragment newInstance(@NonNull Thread thread, @NonNull Post post) {
        EditPostFragment fragment = new EditPostFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(ARG_THREAD, thread);
        bundle.putParcelable(ARG_POST, post);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentEditPostBinding.inflate(inflater, container, false);
        initCreateView(binding.layoutPost);

        mThread = getArguments().getParcelable(ARG_THREAD);
        mPost = getArguments().getParcelable(ARG_POST);

        isHost = mPost != null && mPost.isFirst();
        binding.setHost(isHost);
        L.leaveMsg(String.format("EditPostFragment##post:%s", mPost));

        if (isHost) {
            binding.title.setText(mThread.getTitle());
        }
        
        binding.layoutPost.reply.setText(mPost.getReply());

        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (isHost) {
            App.getPrefComponent().inject(this);
            init();
        }
    }

    @Override
    public void onDestroy() {
        RxJavaUtil.disposeIfNotNull(mDisposable);
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        trackAgent.post(new PageStartEvent(getContext(), "编辑帖子-EditPostFragment"));
    }

    @Override
    public void onPause() {
        trackAgent.post(new PageEndEvent(getContext(), "编辑帖子-EditPostFragment"));
        super.onPause();
    }

    @Override
    protected boolean OnMenuSendClick() {
        String typeId = null;
        if (binding.spinner.getVisibility() == View.VISIBLE) {
            ThreadType selectType = (ThreadType) binding.spinner.getSelectedItem();
            if (selectType == null) {
                showShortSnackbar(R.string.error_not_init);
                return true;
            }
            typeId = selectType.getTypeId();
            //未选择类别
            if (typeId == null || "0".equals(typeId.trim())) {
                showShortSnackbar(R.string.error_no_type_id);
                return true;
            }
        }

        String title = binding.title.getText().toString();
        String message = mReplyView.getText().toString();
        if (!isTitleValid(title) || !isMessageValid(message)) {
            showShortSnackbar(R.string.error_no_title_or_message);
            return true;
        }

        EditPostRequestDialogFragment.newInstance(mThread, mPost, typeId, title, message)
                .show(getFragmentManager(), EditPostRequestDialogFragment.TAG);

        return true;
    }

    @Nullable
    @Override
    public String getCacheKey() {
        return null;
    }

    private boolean isTitleValid(String string) {
        if (!isHost) {
            return true;
        }
        if (TextUtils.isEmpty(string) || string.trim().length() == 0) {
            return false;
        }
        return true;
    }

    private boolean isMessageValid(String string) {
        if (TextUtils.isEmpty(string) || string.trim().length() == 0) {
            return false;
        }
        return true;
    }

    private void init() {
        mDisposable = mS1Service.getNewThreadInfo(Integer.valueOf(mThread.getFid()))
                .map(ThreadType::fromXmlString)
                .compose(RxJavaUtil.iOTransformer())
                .subscribe(types -> {
                            if (types == null) {
                                showRetrySnackbar(getString(R.string.message_network_error), v -> init());
                            } else {
                                setSpinner(types);
                            }
                        }, e -> showRetrySnackbar(ErrorUtil.parse(getContext(), e), v -> init())
                );
    }

    private void setSpinner(@Nullable List<ThreadType> types) {
        if (types == null || types.isEmpty()) {
            binding.spinner.setVisibility(View.GONE);
            return;
        } else {
            binding.spinner.setVisibility(View.VISIBLE);
        }
        SimpleSpinnerAdapter<ThreadType> spinnerAdapter = new SimpleSpinnerAdapter<>(getContext(), types, ThreadType::getTypeName);
        binding.spinner.setAdapter(spinnerAdapter);
        for (int i = 0; i < types.size(); i++) {
            if (TextUtils.equals(types.get(i).getTypeId(), mThread.getTypeId())) {
                binding.spinner.setSelection(i);
                break;
            }
        }
    }

}
