package cl.monsoon.s1next.view.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;

import cl.monsoon.s1next.App;
import cl.monsoon.s1next.R;
import cl.monsoon.s1next.data.pref.ThemeManager;
import cl.monsoon.s1next.event.ThemeChangeEvent;
import cl.monsoon.s1next.singleton.BusProvider;

/**
 * A dialog which used to change theme.
 */
public final class ThemeChangeDialogFragment extends DialogFragment {

    private static final String TAG = ThemeChangeDialogFragment.class.getName();

    public static void showThemeChangeDialog(FragmentActivity fragmentActivity) {
        new ThemeChangeDialogFragment().show(fragmentActivity.getSupportFragmentManager(),
                ThemeChangeDialogFragment.TAG);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Activity activity = getActivity();
        ThemeManager themeManager = App.getAppComponent(activity).getThemeManager();
        int checkedItem = themeManager.getThemeIndex();
        return new AlertDialog.Builder(activity)
                .setTitle(R.string.pref_theme)
                .setSingleChoiceItems(
                        R.array.pref_theme_entries,
                        checkedItem,
                        (dialog, which) -> {
                            // won't change theme if unchanged
                            if (which != checkedItem) {
                                themeManager.applyTheme(which);
                                themeManager.setThemeByIndex(which);

                                BusProvider.get().post(new ThemeChangeEvent());
                            }
                            dismiss();
                        })
                .create();
    }
}
