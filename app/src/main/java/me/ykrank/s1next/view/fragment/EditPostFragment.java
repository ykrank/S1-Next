package me.ykrank.s1next.view.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;
import me.ykrank.s1next.R;
import me.ykrank.s1next.data.api.model.Post;
import me.ykrank.s1next.data.api.model.Thread;
import me.ykrank.s1next.data.api.model.ThreadType;
import me.ykrank.s1next.data.cache.NewThreadCacheModel;
import me.ykrank.s1next.databinding.FragmentEditPostBinding;
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
    private static final String ARG_THREAD_TYPES = "thread_types";

    private static final String CACHE_KEY_PREFIX = "EditPost_%s_%s_%s";

    private Thread mThread;
    private Post mPost;
    private boolean isHost;

    private Disposable mDisposable;

    private FragmentEditPostBinding binding;

    private NewThreadCacheModel cacheModel;

    public static EditPostFragment newInstance(@NonNull Thread thread, @NonNull Post post, ArrayList<ThreadType> threadTypes) {
        EditPostFragment fragment = new EditPostFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(ARG_THREAD, thread);
        bundle.putParcelable(ARG_POST, post);
        bundle.putParcelableArrayList(ARG_THREAD_TYPES, threadTypes);
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
            ArrayList<ThreadType> threadTypes = getArguments().getParcelableArrayList(ARG_THREAD_TYPES);
            if (threadTypes != null) {
                setSpinner(threadTypes);
                for (int i = 0; i < threadTypes.size(); i++) {
                    if (TextUtils.equals(threadTypes.get(i).getTypeId(), mThread.getTypeId())) {
                        binding.spinner.setSelection(i);
                        break;
                    }
                }
            }
        }
        
        binding.layoutPost.reply.setText(mPost.getReply());

        return binding.getRoot();
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

    private void setSpinner(@Nullable List<ThreadType> types) {
        if (types == null || types.isEmpty()) {
            binding.spinner.setVisibility(View.GONE);
            return;
        } else {
            binding.spinner.setVisibility(View.VISIBLE);
        }
        SimpleSpinnerAdapter<ThreadType> spinnerAdapter = new SimpleSpinnerAdapter<>(getContext(), types, ThreadType::getTypeName);
        binding.spinner.setAdapter(spinnerAdapter);
        if (cacheModel != null && types.size() > cacheModel.getSelectPosition()) {
            binding.spinner.setSelection(cacheModel.getSelectPosition());
        }
    }

}
