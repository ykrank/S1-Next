package me.ykrank.s1next.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import me.ykrank.s1next.R;
import me.ykrank.s1next.view.fragment.OpenSourceLicensesFragment;

/**
 * An Activity shows the libraries and files we use in our app.
 */
public final class OpenSourceLicensesActivity extends BaseActivity {
    public static final String TAG = OpenSourceLicensesActivity.class.getName();

    public static void startOpenSourceLicensesActivity(Context context) {
        Intent intent = new Intent(context, OpenSourceLicensesActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_without_drawer);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction().add(R.id.frame_layout,
                    new OpenSourceLicensesFragment()).commit();
        }
    }
}
