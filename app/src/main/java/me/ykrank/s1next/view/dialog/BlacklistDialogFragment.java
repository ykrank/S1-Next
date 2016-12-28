package me.ykrank.s1next.view.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.WindowManager;

import me.ykrank.s1next.R;
import me.ykrank.s1next.data.db.dbmodel.BlackList;
import me.ykrank.s1next.databinding.DialogBlacklistBinding;
import me.ykrank.s1next.viewmodel.BlackListViewModel;
import me.ykrank.s1next.widget.track.event.PageEndEvent;
import me.ykrank.s1next.widget.track.event.PageStartEvent;

/**
 * A dialog lets the user enter thread link/ID to go to that thread.
 */
public final class BlacklistDialogFragment extends BaseDialogFragment {
    public static final int DIALOG_REQUEST_CODE = 11;

    public static final String BLACKLIST_TAG = "blacklist";
    public static final String TAG = BlacklistDialogFragment.class.getName();

    private BlackList mBlacklist;

    public static BlacklistDialogFragment newInstance(BlackList blackList) {
        BlacklistDialogFragment fragment = new BlacklistDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(BLACKLIST_TAG, blackList);
        fragment.setArguments(bundle);

        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Activity activity = getActivity();
        Object blacklist = getArguments().get(BLACKLIST_TAG);
        if (blacklist != null)
            mBlacklist = (BlackList) blacklist;
        setRetainInstance(true);

        DialogBlacklistBinding binding = DataBindingUtil.inflate(activity.getLayoutInflater(),
                R.layout.dialog_blacklist, null, false);
        BlackListViewModel blackListViewModel = new BlackListViewModel();
        if (mBlacklist != null)
            blackListViewModel.blacklist.set(mBlacklist);
        binding.setBlackListViewModel(blackListViewModel);

        AlertDialog alertDialog = new AlertDialog.Builder(activity)
                .setTitle(R.string.menu_thread_go)
                .setView(binding.getRoot())
                .setPositiveButton(android.R.string.ok, (dialog, which) ->
                {
                    String authorIds = binding.blacklistId.getText().toString().trim();
                    int authorId = TextUtils.isEmpty(authorIds) ? 0 : Integer.parseInt(authorIds);
                    String authorName = binding.blacklistName.getText().toString();
                    @BlackList.ForumFLag int forum = binding.switchForum.isChecked() ? BlackList.HIDE_FORUM : BlackList.NORMAL;
                    @BlackList.PostFLag int post = binding.switchForum.isChecked() ? BlackList.HIDE_POST : BlackList.NORMAL;
                    BlackList blackList = new BlackList(authorId, authorName, post, forum);
                    Intent intent = new Intent();
                    intent.putExtra(BLACKLIST_TAG, blackList);
                    getTargetFragment().onActivityResult(DIALOG_REQUEST_CODE, Activity.RESULT_OK, intent);
                })
                .setNegativeButton(android.R.string.cancel, null)
                .create();
        alertDialog.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        return alertDialog;
    }

    @Override
    public void onResume() {
        super.onResume();
        trackAgent.post(new PageStartEvent("弹窗-黑名单-" + TAG));
    }

    @Override
    public void onPause() {
        trackAgent.post(new PageEndEvent("弹窗-黑名单-" + TAG));
        super.onPause();
    }
}
