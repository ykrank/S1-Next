package cl.monsoon.s1next.activity;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.MenuItem;

import cl.monsoon.s1next.R;
import cl.monsoon.s1next.fragment.LoginFragment;

public final class LoginActivity extends AbsThemeActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity);

        // set ToolBar's up icon to cross
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(R.attr.menuCross, typedValue, true);
        toolbar.setNavigationIcon(typedValue.resourceId);

        if (savedInstanceState == null) {
            Fragment fragment = new LoginFragment();

            getFragmentManager().beginTransaction()
                    .replace(R.id.frame_layout, fragment, LoginFragment.TAG).commit();
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
