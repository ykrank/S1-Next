package me.ykrank.s1next.view.dialog;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;

import javax.inject.Inject;

import me.ykrank.s1next.App;
import me.ykrank.s1next.widget.track.DataTrackAgent;

/**
 * Created by ykrank on 2016/12/28.
 */

public abstract class BaseDialogFragment extends DialogFragment {
    @Inject
    DataTrackAgent trackAgent;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.getAppComponent().inject(this);
    }
}
