package me.ykrank.s1next.view.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.WindowManager;

import me.ykrank.s1next.R;
import me.ykrank.s1next.databinding.DialogFavouritesAddBinding;
import me.ykrank.s1next.util.ViewUtil;

/**
 * A dialog lets user enter remark if user want to add this thread to his/her favourites.
 * Clicks the positive button can let user add this thread to his/her favourites.
 */
public final class ThreadFavouritesAddDialogFragment extends DialogFragment {

    public static final String TAG = ThreadFavouritesAddDialogFragment.class.getName();

    private static final String ARG_THREAD_ID = "thread_id";

    public static ThreadFavouritesAddDialogFragment newInstance(String threadId) {
        ThreadFavouritesAddDialogFragment fragment = new ThreadFavouritesAddDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_THREAD_ID, threadId);
        fragment.setArguments(bundle);

        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        DialogFavouritesAddBinding binding = DataBindingUtil.inflate(getActivity().getLayoutInflater(),
                R.layout.dialog_favourites_add, null, false);

        AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                .setTitle(R.string.menu_favourites_add)
                .setView(binding.getRoot())
                .setPositiveButton(R.string.dialog_button_text_add, null)
                .setNegativeButton(android.R.string.cancel, null)
                .create();
        alertDialog.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        // http://stackoverflow.com/a/7636468
        alertDialog.setOnShowListener(dialog -> {
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v ->
                    ThreadFavouritesAddRequestDialogFragment.newInstance(
                            getArguments().getString(ARG_THREAD_ID),
                            binding.remark.getText().toString())
                            .show(getFragmentManager(), ThreadFavouritesAddRequestDialogFragment.TAG));
            ViewUtil.consumeRunnableWhenImeActionPerformed(binding.remark, () ->
                    alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).performClick());
        });
        return alertDialog;
    }
}
