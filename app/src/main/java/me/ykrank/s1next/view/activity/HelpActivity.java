package me.ykrank.s1next.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.webkit.WebView;

import androidx.annotation.StyleRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import me.ykrank.s1next.App;
import me.ykrank.s1next.R;
import me.ykrank.s1next.view.fragment.HelpFragment;
import me.ykrank.s1next.view.internal.ToolbarDelegate;
import me.ykrank.s1next.widget.track.event.ViewHelpTrackEvent;

/**
 * An Activity shows a help page.
 */
public final class HelpActivity extends AppCompatActivity {
    private static final String ARG_STYLE = "style";
    private ToolbarDelegate mToolbarDelegate;

    private HelpFragment mHelpFragment;

    public static void startHelpActivity(Context context, @StyleRes int styleId) {
        App.Companion.get().getTrackAgent().post(new ViewHelpTrackEvent());
        Intent intent = new Intent(context, HelpActivity.class);
        intent.putExtra(ARG_STYLE, styleId);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        int styleId = getIntent().getIntExtra(ARG_STYLE, -1);
        if (styleId != -1) {
            setTheme(styleId);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_without_drawer_and_scrolling_effect);
        setupToolbar();

        if (savedInstanceState == null) {
            mHelpFragment = HelpFragment.getInstance();
            getSupportFragmentManager().beginTransaction().add(R.id.frame_layout, mHelpFragment,
                    HelpFragment.TAG).commit();
        } else {
            mHelpFragment = (HelpFragment) getSupportFragmentManager().findFragmentByTag(
                    HelpFragment.TAG);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        WebView webView = mHelpFragment.getWebView();
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            mToolbarDelegate = new ToolbarDelegate(this, toolbar);
        }
    }
}
