package me.ykrank.s1next.view.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.EditText;

import me.ykrank.s1next.util.L;
import me.ykrank.s1next.view.dialog.PmRequestDialogFragment;
import me.ykrank.s1next.view.dialog.ReplyRequestDialogFragment;

/**
 * A Fragment shows {@link EditText} to let the user pm.
 */
public final class NewPmFragment extends BasePostFragment {

    public static final String TAG = NewPmFragment.class.getName();

    private static final String ARG_TO_UID = "arg_to_uid";

    private String mToUid;

    public static NewPmFragment newInstance(@NonNull String toUid) {
        NewPmFragment fragment = new NewPmFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_TO_UID, toUid);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mToUid = getArguments().getString(ARG_TO_UID);
        L.leaveMsg("NewPmFragment##mToUid"+mToUid);
    }

    @Override
    protected boolean OnMenuSendClick() {
        PmRequestDialogFragment.newInstance(mToUid, mReplyView.getText().toString()).show(getFragmentManager(),
                ReplyRequestDialogFragment.TAG);

        return true;
    }
}
