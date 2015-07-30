package cl.monsoon.s1next.view.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

import cl.monsoon.s1next.R;
import cl.monsoon.s1next.data.api.model.collection.Posts;
import cl.monsoon.s1next.view.adapter.ThreadAttachmentInfoListArrayAdapter;

/**
 * A dialog shows attachment.
 */
public final class ThreadAttachmentDialogFragment extends DialogFragment {

    public static final String TAG = ThreadAttachmentDialogFragment.class.getName();

    private static final String ARG_ATTACHMENT_TITLE = "attachment_title";
    private static final String ARG_THREAD_ATTACHMENT_INFO_LIST = "thread_attachment_info_list";

    public static ThreadAttachmentDialogFragment newInstance(Posts.ThreadAttachment threadAttachment) {
        ThreadAttachmentDialogFragment fragment = new ThreadAttachmentDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_ATTACHMENT_TITLE, threadAttachment.getTitle());
        bundle.putParcelableArrayList(ARG_THREAD_ATTACHMENT_INFO_LIST, threadAttachment.getInfoList());
        fragment.setArguments(bundle);

        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity())
                .setTitle(getArguments().getString(ARG_ATTACHMENT_TITLE))
                .setAdapter(new ThreadAttachmentInfoListArrayAdapter(getActivity(),
                        R.layout.item_two_line_text, getArguments().getParcelableArrayList(
                        ARG_THREAD_ATTACHMENT_INFO_LIST)), null)
                .setPositiveButton(R.string.dialog_button_text_done, null)
                .create();
    }
}
