package cl.monsoon.s1next.activity;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import cl.monsoon.s1next.Config;
import cl.monsoon.s1next.R;
import cl.monsoon.s1next.fragment.ReplyFragment;

/**
 * An Activity to send a reply.
 */
public class ReplyActivity extends ActionBarActivity {

    public final static String ARG_THREAD_TITLE = "thread_title";
    public final static String ARG_THREAD_ID = "thread_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // default theme is TranslucentDarkTheme
        if (Config.getTheme() == Config.LIGHT_THEME) {
            setTheme(Config.TRANSLUCENT_LIGHT_THEME);
        } else {
            setTheme(Config.TRANSLUCENT_DARK_THEME);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply);

        CharSequence title = getIntent().getStringExtra(ARG_THREAD_TITLE);
        String url = getIntent().getStringExtra(ARG_THREAD_ID);
        if (savedInstanceState == null) {
            Fragment fragment = ReplyFragment.newInstance(title, url);

            getFragmentManager().beginTransaction()
                    .replace(R.id.frame_layout, fragment, ReplyFragment.TAG).commit();
        }
    }
}
