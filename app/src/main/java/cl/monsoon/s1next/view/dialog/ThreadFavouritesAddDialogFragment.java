package cl.monsoon.s1next.view.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import cl.monsoon.s1next.R;

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
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_favourites_add,
                (ViewGroup) getView(), false);

        AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.dialog_title_favourites_add)
                .setView(view)
                .setPositiveButton(R.string.dialog_button_text_add, null)
                .setNegativeButton(android.R.string.cancel, null)
                .create();
        alertDialog.setOnShowListener(dialog ->
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v ->
                        ThreadFavouritesAddRequestDialogFragment.newInstance(
                                getArguments().getString(ARG_THREAD_ID),
                                ((EditText) view.findViewById(R.id.remark)).getText().toString())
                                .show(getFragmentManager(),
                                        ThreadFavouritesAddRequestDialogFragment.TAG)));
        return alertDialog;
    }
}
