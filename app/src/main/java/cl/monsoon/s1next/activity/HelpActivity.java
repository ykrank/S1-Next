package cl.monsoon.s1next.activity;

import android.os.Bundle;
import android.webkit.WebView;

import cl.monsoon.s1next.R;
import cl.monsoon.s1next.fragment.HelpFragment;

public final class HelpActivity extends BaseActivity {

    private HelpFragment mHelpFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_without_drawer);

        if (savedInstanceState == null) {
            mHelpFragment = new HelpFragment();

            getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, mHelpFragment,
                    HelpFragment.TAG).commit();
        } else {
            mHelpFragment = (HelpFragment) getSupportFragmentManager().findFragmentByTag(
                    HelpFragment.TAG);
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
}
