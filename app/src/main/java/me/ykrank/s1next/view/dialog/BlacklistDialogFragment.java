package me.ykrank.s1next.view.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.WindowManager;

import me.ykrank.s1next.R;
import me.ykrank.s1next.data.db.dbmodel.BlackList;
import me.ykrank.s1next.databinding.DialogBlacklistBinding;
import me.ykrank.s1next.view.internal.RequestCode;
import me.ykrank.s1next.viewmodel.BlackListViewModel;

/**
 * A dialog lets the user add or edit blacklist.
 */
public final class BlacklistDialogFragment extends BaseDialogFragment {

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
        if (blacklist != null) {
            mBlacklist = (BlackList) blacklist;
        }

        DialogBlacklistBinding binding = DataBindingUtil.inflate(activity.getLayoutInflater(),
                R.layout.dialog_blacklist, null, false);
        BlackListViewModel blackListViewModel = new BlackListViewModel();
        if (mBlacklist != null)
            blackListViewModel.blacklist.set(mBlacklist);
        binding.setBlackListViewModel(blackListViewModel);

        AlertDialog alertDialog = new AlertDialog.Builder(activity)
                .setTitle(mBlacklist == null ? R.string.menu_blacklist_add : R.string.menu_blacklist_edit)
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
                    getTargetFragment().onActivityResult(RequestCode.REQUEST_CODE_BLACKLIST, Activity.RESULT_OK, intent);
                })
                .setNegativeButton(android.R.string.cancel, null)
                .create();
        alertDialog.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        return alertDialog;
    }
}
