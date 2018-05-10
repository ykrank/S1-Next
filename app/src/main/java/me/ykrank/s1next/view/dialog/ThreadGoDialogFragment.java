package me.ykrank.s1next.view.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.github.ykrank.androidtools.guava.Optional;
import com.github.ykrank.androidtools.util.ViewUtil;

import me.ykrank.s1next.R;
import me.ykrank.s1next.data.api.model.ThreadLink;
import me.ykrank.s1next.databinding.DialogThreadGoBinding;
import me.ykrank.s1next.view.activity.PostListActivity;

/**
 * A dialog lets the user enter thread link/ID to go to that thread.
 */
public final class ThreadGoDialogFragment extends BaseDialogFragment {

    public static final String TAG = ThreadGoDialogFragment.class.getName();

    private Optional<ThreadLink> mThreadLink;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Activity activity = getActivity();
        DialogThreadGoBinding binding = DataBindingUtil.inflate(activity.getLayoutInflater(),
                R.layout.dialog_thread_go, null, false);
        TextInputLayout threadLinkOrIdWrapperView = binding.threadLinkOrIdWrapper;
        EditText threadLinkOrIdView = binding.threadLinkOrId;

        AlertDialog alertDialog = new AlertDialog.Builder(activity)
                .setTitle(R.string.menu_thread_go)
                .setView(binding.getRoot())
                .setPositiveButton(R.string.dialog_button_text_go, (dialog, which) ->
                        PostListActivity.startPostListActivity(activity, mThreadLink.get(), false))
                .setNegativeButton(android.R.string.cancel, null)
                .create();
        alertDialog.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        // http://stackoverflow.com/a/7636468
        alertDialog.setOnShowListener(dialog -> {
            Button positionButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
            ViewUtil.consumeRunnableWhenImeActionPerformed(binding.threadLinkOrId, () -> {
                if (positionButton.isEnabled()) {
                    positionButton.performClick();
                } else {
                    if (threadLinkOrIdWrapperView.getError() == null) {
                        threadLinkOrIdWrapperView.setError(getResources().getText(
                                R.string.error_field_invalid_or_unsupported_thread_link_or_id));
                    }
                }
            });

            threadLinkOrIdView.addTextChangedListener(new TextWatcher() {

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    String threadLinkOrId = s.toString();
                    if (!TextUtils.isEmpty(threadLinkOrId)) {
                        mThreadLink = ThreadLink.parse2(threadLinkOrId);
                        if (mThreadLink.isPresent()) {
                            threadLinkOrIdWrapperView.setError(null);
                            positionButton.setEnabled(true);
                        } else {
                            if (threadLinkOrIdWrapperView.getError() == null) {
                                threadLinkOrIdWrapperView.setError(getResources().getText(
                                        R.string.error_field_invalid_or_unsupported_thread_link_or_id));
                            }
                            positionButton.setEnabled(false);
                        }
                    }
                }
            });
            // check whether we need to disable position button when this dialog shows
            if (TextUtils.isEmpty(threadLinkOrIdView.getText())) {
                positionButton.setEnabled(false);
            } else {
                threadLinkOrIdView.setText(threadLinkOrIdView.getText());
            }
        });
        return alertDialog;
    }
}
