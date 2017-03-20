package me.ykrank.s1next.view.fragment;

import android.databinding.DataBindingUtil;
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
import me.ykrank.s1next.data.api.model.RatePreInfo;
import me.ykrank.s1next.databinding.FragmentNewRateBinding;
import me.ykrank.s1next.util.ErrorUtil;
import me.ykrank.s1next.util.L;
import me.ykrank.s1next.util.RxJavaUtil;
import me.ykrank.s1next.view.adapter.SimpleSpinnerAdapter;
import me.ykrank.s1next.viewmodel.NewRateViewModel;
import me.ykrank.s1next.widget.track.event.page.PageEndEvent;
import me.ykrank.s1next.widget.track.event.page.PageStartEvent;

/**
 * A Fragment shows {@link EditText} to let the user enter reply.
 */
public final class NewRateFragment extends BaseFragment {

    public static final String TAG = NewRateFragment.class.getName();

    private static final String ARG_THREAD_ID = "thread_id";
    private static final String ARG_POST_ID = "post_id";

    @Inject
    S1Service mS1Service;
    private String threadId, postID;
    private Disposable mDisposable;

    private FragmentNewRateBinding binding;

    public static NewRateFragment newInstance(String threadId, String postId) {
        NewRateFragment fragment = new NewRateFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_THREAD_ID, threadId);
        bundle.putString(ARG_POST_ID, postId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_new_rate, container, false);
        binding.setModel(new NewRateViewModel());
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        threadId = getArguments().getString(ARG_THREAD_ID);
        postID = getArguments().getString(ARG_POST_ID);
        L.leaveMsg("NewRateFragment##threadId:" + threadId + ",postID:" + postID);

        App.getPrefComponent().inject(this);
        init();
    }

    @Override
    public void onDestroy() {
        RxJavaUtil.disposeIfNotNull(mDisposable);
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        trackAgent.post(new PageStartEvent(getContext(), "评分-NewRateFragment"));
    }

    @Override
    public void onPause() {
        trackAgent.post(new PageEndEvent(getContext(), "评分-NewRateFragment"));
        super.onPause();
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
        mDisposable = mS1Service.getRatePreInfo(threadId, postID, System.currentTimeMillis())
                .map(RatePreInfo::fromHtml)
                .compose(RxJavaUtil.iOTransformer())
                .subscribe(info -> {
                    binding.getModel().info.set(info);
                    setSpinner(info.getScoreChoices());
                    L.d(info.toString());
                        }, e -> showRetrySnackbar(ErrorUtil.parse(getContext(), e), v -> init())
                );
    }

    private void setSpinner(@NonNull List<String> choices) {
        SimpleSpinnerAdapter<String> spinnerAdapter = new SimpleSpinnerAdapter<>(getContext(), choices, String::valueOf);
        binding.spinner.setAdapter(spinnerAdapter);
    }
}
