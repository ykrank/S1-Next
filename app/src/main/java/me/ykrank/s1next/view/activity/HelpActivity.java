package me.ykrank.s1next.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.webkit.WebView;

import me.ykrank.s1next.R;
import me.ykrank.s1next.view.fragment.HelpFragment;
import me.ykrank.s1next.view.internal.ToolbarDelegate;

/**
 * An Activity shows a help page.
 * 为了防止WebView内存泄露,应该在新进程中打开
 */
public final class HelpActivity extends OriginActivity {
    private ToolbarDelegate mToolbarDelegate;

    private HelpFragment mHelpFragment;

    public static void startHelpActivity(Context context) {
        Intent intent = new Intent(context, HelpActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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

    /**
     * 为了防止内存泄露，退出时直接退出进程
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.exit(0);
    }

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            mToolbarDelegate = new ToolbarDelegate(this, toolbar);
        }
    }
}
