package me.ykrank.s1next.viewmodel;

import android.view.View;
import android.widget.TextView;

import androidx.databinding.ObservableField;

import com.google.android.material.snackbar.Snackbar;

import me.ykrank.s1next.data.db.dbmodel.BlackList;

import static com.github.ykrank.androidtools.ui.internal.CoordinatorLayoutAnchorDelegateBaseImpl.SNACK_BAR_MAX_LINE;


public final class BlackListViewModel {

    public final ObservableField<BlackList> blacklist = new ObservableField<>();

    public View.OnClickListener clickSnackbar() {
        return v -> {
            Snackbar snackbar = Snackbar.make(v.getRootView(), ((TextView) v).getText(), Snackbar.LENGTH_SHORT);
            TextView textView = snackbar.getView().findViewById(com.google.android.material.R.id.snackbar_text);
            textView.setMaxLines(SNACK_BAR_MAX_LINE);
            snackbar.show();
        };
    }
}
