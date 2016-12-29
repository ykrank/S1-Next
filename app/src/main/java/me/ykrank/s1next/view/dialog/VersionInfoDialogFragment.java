package me.ykrank.s1next.view.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;

import me.ykrank.s1next.R;
import me.ykrank.s1next.databinding.DialogVersionInfoBinding;
import me.ykrank.s1next.widget.track.event.page.PageEndEvent;
import me.ykrank.s1next.widget.track.event.page.PageStartEvent;

/**
 * A dialog shows version info.
 */
public final class VersionInfoDialogFragment extends BaseDialogFragment {

    public static final String TAG = VersionInfoDialogFragment.class.getName();

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        DialogVersionInfoBinding binding = DataBindingUtil.inflate(getActivity().getLayoutInflater(),
                R.layout.dialog_version_info, null, false);

        return new AlertDialog.Builder(getContext())
                .setView(binding.getRoot())
                .create();
    }

    @Override
    public void onResume() {
        super.onResume();
        trackAgent.post(new PageStartEvent(getContext(), "弹窗-版本信息-"));
    }

    @Override
    public void onPause() {
        trackAgent.post(new PageEndEvent(getContext(), "弹窗-版本信息-"));
        super.onPause();
    }
}
