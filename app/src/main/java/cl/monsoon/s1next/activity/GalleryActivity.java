package cl.monsoon.s1next.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.MenuItem;

import cl.monsoon.s1next.R;
import cl.monsoon.s1next.fragment.GalleryFragment;

/**
 * An Activity shows an ImageView that supports multi-touch.
 */
public final class GalleryActivity extends ActionBarActivity {

    public static final String ARG_IMAGE_URL = "image_url";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(null);

        // set ToolBar's up icon to cross
        TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(R.attr.menuCross, typedValue, true);
        toolbar.setNavigationIcon(typedValue.resourceId);

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
}
