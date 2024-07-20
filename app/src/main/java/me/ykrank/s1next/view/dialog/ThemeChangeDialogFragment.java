package me.ykrank.s1next.view.dialog;

import android.app.Dialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.appcompat.app.AlertDialog;

import com.github.ykrank.androidtools.widget.EventBus;

import javax.inject.Inject;

import me.ykrank.s1next.App;
import me.ykrank.s1next.R;
import me.ykrank.s1next.data.pref.ThemeManager;
import me.ykrank.s1next.view.event.ThemeChangeEvent;

/**
 * A dialog which used to change theme.
 */
public final class ThemeChangeDialogFragment extends BaseDialogFragment {

    private static final String TAG = ThemeChangeDialogFragment.class.getName();

    @Inject
    EventBus mEventBus;

    @Inject
    ThemeManager mThemeManager;

    public static void showThemeChangeDialog(FragmentActivity fragmentActivity) {
        new ThemeChangeDialogFragment().show(fragmentActivity.getSupportFragmentManager(),
                ThemeChangeDialogFragment.TAG);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        App.Companion.getAppComponent().inject(this);
        int checkedItem = mThemeManager.getThemeIndex();
        return new AlertDialog.Builder(requireContext())
                .setTitle(R.string.pref_theme)
                .setSingleChoiceItems(R.array.pref_theme_entries, checkedItem, (dialog, which) -> {
                    // won't change theme if unchanged
                    if (which != checkedItem) {
                        mThemeManager.applyTheme(which);
                        mThemeManager.setThemeByIndex(which);
                        mEventBus.postDefault(new ThemeChangeEvent());
                    }
                    dismiss();
                })
                .create();
    }
}
