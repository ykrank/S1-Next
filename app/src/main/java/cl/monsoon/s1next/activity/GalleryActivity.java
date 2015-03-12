package cl.monsoon.s1next.activity;

import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import cl.monsoon.s1next.R;
import cl.monsoon.s1next.fragment.GalleryFragment;
import cl.monsoon.s1next.util.ResourceUtil;
import cl.monsoon.s1next.widget.InsetsFrameLayout;

/**
 * An Activity shows an ImageView that supports multi-touch.
 */
public final class GalleryActivity extends ActionBarActivity implements InsetsFrameLayout.OnInsetsCallback {

    public static final String ARG_IMAGE_URL = "image_url";

    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        setTitle(null);
        // set Toolbar's icon to cross
        mToolbar.setNavigationIcon(ResourceUtil.getResourceId(getTheme(), R.attr.menuCross));

        InsetsFrameLayout insetsFrameLayout =
                (InsetsFrameLayout) findViewById(R.id.insets_frame_layout);
        insetsFrameLayout.setOnInsetsCallback(this);

        String url = getIntent().getStringExtra(ARG_IMAGE_URL);
        if (savedInstanceState == null) {
            Fragment fragment = GalleryFragment.newInstance(url);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_layout, fragment, GalleryFragment.TAG).commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * @see cl.monsoon.s1next.activity.BaseActivity#onInsetsChanged(android.graphics.Rect)
     */
    @Override
    public void onInsetsChanged(@NonNull Rect insets) {
        mToolbar.setPadding(0, insets.top, 0, 0);
        mToolbar.getLayoutParams().height = insets.top + ResourceUtil.getToolbarHeight();
        mToolbar.requestLayout();
    }
}
