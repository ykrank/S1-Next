package me.ykrank.s1next.view.activity;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.google.common.base.Optional;

import javax.inject.Inject;

import me.ykrank.s1next.App;
import me.ykrank.s1next.R;
import me.ykrank.s1next.data.api.model.ThreadLink;
import me.ykrank.s1next.data.pref.ThemeManager;
import me.ykrank.s1next.view.dialog.QuotePostPageParserDialogFragment;
import me.ykrank.s1next.view.dialog.ThreadLinkInvalidPromptDialogFragment;

/**
 * An Activity to detect whether the thread link (URI) from Intent is valid.
 * Also show prompt if the thread corresponding to url do not exist.
 * <p>
 * This Activity is only used for Intent filter.
 */
public final class PostListGatewayActivity extends FragmentActivity {

    @Inject
    ThemeManager mThemeManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // default theme for this Activity is light theme
        if (App.getAppComponent(this).getThemeManager().isDarkTheme()) {
            setTheme(ThemeManager.TRANSLUCENT_THEME_DARK);
        }

        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            Uri uri = getIntent().getData();
            Optional<ThreadLink> threadLink = ThreadLink.parse(uri.toString());
            if (threadLink.isPresent()) {
                ThreadLink threadLinkInstance = threadLink.get();
                if (threadLinkInstance.getQuotePostId().isPresent()) {
                    QuotePostPageParserDialogFragment.newInstance(threadLinkInstance).show(
                            getSupportFragmentManager(), QuotePostPageParserDialogFragment.TAG);
                } else {
                    PostListActivity.startPostListActivity(this, threadLinkInstance);
                    finish();
                }
            } else {
                ThreadLinkInvalidPromptDialogFragment.newInstance(this,
                        R.string.dialog_message_invalid_or_unsupported_link).show(
                        getSupportFragmentManager(), ThreadLinkInvalidPromptDialogFragment.TAG);
            }
        }
    }
}
