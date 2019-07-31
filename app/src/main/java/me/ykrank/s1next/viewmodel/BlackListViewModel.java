package me.ykrank.s1next.viewmodel;

import androidx.databinding.ObservableField;
import com.google.android.material.snackbar.Snackbar;
import android.view.View;
import android.widget.TextView;

import me.ykrank.s1next.data.db.dbmodel.BlackList;


public final class BlackListViewModel {

    public final ObservableField<BlackList> blacklist = new ObservableField<>();

    public View.OnClickListener clickSnackbar() {
        return v -> Snackbar.make(v.getRootView(), ((TextView) v).getText(),
                Snackbar.LENGTH_SHORT)
                .show();
    }
}
