package cl.monsoon.s1next.viewmodel;

import android.databinding.ObservableField;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.TextView;

import cl.monsoon.s1next.data.db.dbmodel.BlackList;
import cl.monsoon.s1next.view.activity.ThreadListActivity;


public final class BlackListViewModel {

    public final ObservableField<BlackList> blacklist = new ObservableField<>();

    public View.OnClickListener clickSnackbar() {
        return v -> {
            Snackbar.make(v.getRootView(), ((TextView)v).getText(),
                    Snackbar.LENGTH_SHORT)
                    .show();
        };
    }
}
