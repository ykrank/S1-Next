package me.ykrank.s1next.view.fragment;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.disposables.Disposable;
import me.ykrank.s1next.App;
import me.ykrank.s1next.R;
import me.ykrank.s1next.data.User;
import me.ykrank.s1next.data.api.S1Service;
import me.ykrank.s1next.data.api.model.RatePreInfo;
import me.ykrank.s1next.databinding.FragmentNewRateBinding;
import me.ykrank.s1next.databinding.ItemRateReasonBinding;
import me.ykrank.s1next.util.ErrorUtil;
import me.ykrank.s1next.util.L;
import me.ykrank.s1next.util.RxJavaUtil;
import me.ykrank.s1next.view.adapter.SimpleSpinnerAdapter;
import me.ykrank.s1next.view.adapter.simple.BindViewHolderCallback;
import me.ykrank.s1next.view.adapter.simple.SimpleRecycleViewAdapter;
import me.ykrank.s1next.view.dialog.RateRequestDialogFragment;
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
    S1Service s1Service;
    @Inject
    User mUser;
    private String threadId, postID;
    private RatePreInfo ratePreInfo;
    private Disposable mDisposable;

    private FragmentNewRateBinding binding;
    private SimpleRecycleViewAdapter reasonAdapter;
    private BindViewHolderCallback bindViewHolderCallback;

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
        setHasOptionsMenu(true);

        threadId = getArguments().getString(ARG_THREAD_ID);
        postID = getArguments().getString(ARG_POST_ID);
        L.leaveMsg("NewRateFragment##threadId:" + threadId + ",postID:" + postID);

        App.getPrefComponent().inject(this);
        init();
        refreshData();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_new_rate, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_send:
                postRate();
                return true;
        }
        return super.onOptionsItemSelected(item);
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

    private String getScore() {
        return (String) binding.spinner.getSelectedItem();
    }

    private String getReason() {
        return binding.etReason.getText().toString();
    }

    private boolean isScoreValid() {
        String score = getScore();
        if (TextUtils.isEmpty(score) || "0".equals(score.trim())) {
            return false;
        }
        return true;
    }

    private void init() {
        bindViewHolderCallback = bind -> {
            ItemRateReasonBinding itemBinding = (ItemRateReasonBinding) bind;
            itemBinding.getRoot().setOnClickListener(v -> {
                binding.etReason.setText(itemBinding.getModel());
            });
        };

        binding.recycleView.setLayoutManager(new LinearLayoutManager(getContext()));
        reasonAdapter = new SimpleRecycleViewAdapter(getContext(), R.layout.item_rate_reason, bindViewHolderCallback);
        binding.recycleView.setAdapter(reasonAdapter);
    }

    private void refreshData() {
        reasonAdapter.setHasProgress(true);

        mDisposable = s1Service.getRatePreInfo(threadId, postID, System.currentTimeMillis())
                .map(RatePreInfo::fromHtml)
                .compose(RxJavaUtil.iOTransformer())
                .subscribe(info -> {
                    ratePreInfo = info;
                    if (!TextUtils.isEmpty(info.getAlertError())) {
                        reasonAdapter.setHasProgress(false);
                        showRetrySnackbar(info.getAlertError(), v -> refreshData());
                    } else {
                        binding.getModel().info.set(info);
                        setSpinner(info.getScoreChoices());
                        setReasonRecycleView(info.getReasons());
                    }
                        }, e -> {
                            reasonAdapter.setHasProgress(false);
                    showRetrySnackbar(ErrorUtil.parse(getContext(), e), v -> refreshData());
                        }
                );
    }

    private void setSpinner(@NonNull List<String> choices) {
        SimpleSpinnerAdapter<String> spinnerAdapter = new SimpleSpinnerAdapter<>(getContext(), choices, String::valueOf);
        binding.spinner.setAdapter(spinnerAdapter);
    }

    private void setReasonRecycleView(@NonNull List<String> reasons) {
        reasonAdapter.refreshDataSet(reasons, false);
    }

    private void postRate() {
        if (ratePreInfo == null) {
            showShortSnackbar(R.string.error_not_init);
            return;
        }
        if (!isScoreValid()) {
            showShortSnackbar(R.string.invalid_score);
            return;
        }
        RateRequestDialogFragment.newInstance(ratePreInfo, getScore(), getReason()).show(getFragmentManager(),
                RateRequestDialogFragment.TAG);
    }
}
