package me.ykrank.s1next.view.internal;

import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.google.common.base.Preconditions;

import me.ykrank.s1next.R;
import me.ykrank.s1next.databinding.PopWindowTitleBinding;
import me.ykrank.s1next.util.ResourceUtil;

/**
 * This class represents a delegate which you can use to add
 * {@link Toolbar} to {@link AppCompatActivity}.
 */
public final class ToolbarDelegate {

    private final AppCompatActivity mAppCompatActivity;
    private final Toolbar mToolbar;
    @Nullable
    private TextView longTitleView;

    public ToolbarDelegate(AppCompatActivity appCompatActivity, Toolbar toolbar) {
        this.mAppCompatActivity = appCompatActivity;
        this.mToolbar = toolbar;

        setUpToolbar();
    }

    /**
     * Sets a {@link android.widget.Toolbar Toolbar} to act as the {@link android.support.v7.app.ActionBar}
     * for this Activity window.
     * Also displays home as an "up" affordance in Toolbar.
     */
    private void setUpToolbar() {
        // designate a Toolbar as the ActionBar
        mAppCompatActivity.setSupportActionBar(mToolbar);
        Preconditions.checkNotNull(mAppCompatActivity.getSupportActionBar())
                .setDisplayHomeAsUpEnabled(true);
        //self defined title TextView
        longTitleView = (TextView) mToolbar.findViewById(R.id.toolbar_title_long);
        if (longTitleView != null) {
            longTitleView.setOnLongClickListener(v -> onLongClick(v, longTitleView.getText().toString()));
        }
    }

    /**
     * Sets Toolbar's navigation icon to cross.
     */
    public void setupNavCrossIcon() {
        mToolbar.setNavigationIcon(ResourceUtil.getResourceId(mAppCompatActivity.getTheme(),
                R.attr.iconClose));
    }

    public Toolbar getToolbar() {
        return mToolbar;
    }

    /**
     * Set title
     *
     * @return whether handle this action
     */
    public boolean setTitle(CharSequence title) {
        if (longTitleView != null && !TextUtils.equals(longTitleView.getText(), title)) {
            mToolbar.setTitle(null);
            longTitleView.setText(title);
            return true;
        }
        return false;
    }

    private boolean onLongClick(View anchor, String title) {
        PopWindowTitleBinding binding = PopWindowTitleBinding.inflate(LayoutInflater.from(anchor.getContext()));
        binding.setTitle(title);
        final PopupWindow popupWindow = new PopupWindow(binding.getRoot(),
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(false);
        popupWindow.showAsDropDown(anchor);
        return true;
    }
}
