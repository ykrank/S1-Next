package cl.monsoon.s1next.view.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import cl.monsoon.s1next.R;
import cl.monsoon.s1next.view.fragment.GalleryFragment;
import cl.monsoon.s1next.view.internal.ToolbarPresenter;

/**
 * An Activity shows an ImageView that supports multi-touch.
 */
public final class GalleryActivity extends AppCompatActivity {

    public static final String ARG_IMAGE_URL = "image_url";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ToolbarPresenter toolbarPresenter = new ToolbarPresenter(this, toolbar);
        setTitle(null);
        toolbarPresenter.setupNavCrossIcon();

        // set Toolbar's padding because we use `android:windowTranslucentStatus` in this Activity
        ViewCompat.setOnApplyWindowInsetsListener(toolbar, (v, insets) -> {
            int top = insets.getSystemWindowInsetTop();
            v.setPadding(0, top, 0, 0);
            v.getLayoutParams().height = v.getContext().getResources().getDimensionPixelSize(
                    R.dimen.abc_action_bar_default_height_material) + top;

            return insets.consumeSystemWindowInsets();
        });

        String url = getIntent().getStringExtra(ARG_IMAGE_URL);
        if (savedInstanceState == null) {
            Fragment fragment = GalleryFragment.newInstance(url);

            getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, fragment,
                    GalleryFragment.TAG).commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();

                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
