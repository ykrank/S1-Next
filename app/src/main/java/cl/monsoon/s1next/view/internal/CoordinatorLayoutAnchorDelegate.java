package cl.monsoon.s1next.view.internal;

import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;

import com.google.common.base.Optional;

/**
 * This class represents a delegate which you can use to
 * add {@link FloatingActionButton} or {@link Snackbar}
 * to {@link CoordinatorLayout} with Anchor.
 */
public interface CoordinatorLayoutAnchorDelegate {

    void setupFloatingActionButton(@DrawableRes int resId, View.OnClickListener onClickListener);

    /**
     * Show a short {@link Snackbar} if current {@link android.app.Activity} is visible,
     * otherwise show a short {@link android.widget.Toast}.
     *
     * @param text The text to show.
     * @return The displayed {@code Optional.of(snackbar)} if we use {@link Snackbar} to
     * show short text, otherwise the {@code Optional.absent()}.
     */
    Optional<Snackbar> showShortText(CharSequence text);

    /**
     * Show a short {@link Snackbar}.
     *
     * @param resId The resource id of the string resource to show for {@link Snackbar}.
     * @return The displayed {@code Optional.of(snackbar)}.
     */
    Optional<Snackbar> showShortSnackbar(@StringRes int resId);

    /**
     * Show a short {@link Snackbar}.
     *
     * @param text text The text to show.
     * @return The displayed {@code Optional.of(snackbar)}.
     */
    Optional<Snackbar> showShortSnackbar(CharSequence text);

    /**
     * Show a {@link Snackbar} if current {@link android.app.Activity} is visible.
     *
     * @param text            The text to show.
     * @param actionResId     The action string resource to display.
     * @param onClickListener Callback to be invoked when the action is clicked.
     * @return The displayed {@code Optional.of(snackbar)} if current {@link android.app.Activity}
     * is visible, otherwise the {@code Optional.absent()}.
     */
    Optional<Snackbar> showLongSnackbarIfVisible(CharSequence text, @StringRes int actionResId, View.OnClickListener onClickListener);

    /**
     * Dismiss the {@link Snackbar} if {@link CoordinatorLayout} has Snackbar.
     */
    void dismissSnackbarIfExist();
}
