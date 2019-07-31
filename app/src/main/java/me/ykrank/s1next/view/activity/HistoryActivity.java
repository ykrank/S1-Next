package me.ykrank.s1next.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import com.github.ykrank.androidtools.util.L;

import me.ykrank.s1next.R;
import me.ykrank.s1next.view.fragment.HistoryListFragment;
import me.ykrank.s1next.widget.track.event.ViewHistoryTrackEvent;

/**
 * Activity show post view history list
 */
public class HistoryActivity extends BaseActivity {

    public static void start(Context context) {
        Intent intent = new Intent(context, HistoryActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_without_drawer_and_scrolling_effect);

        trackAgent.post(new ViewHistoryTrackEvent());
        L.leaveMsg("HistoryActivity");

        if (savedInstanceState == null) {
            Fragment fragment = HistoryListFragment.newInstance();
            getSupportFragmentManager().beginTransaction().add(R.id.frame_layout, fragment,
                    HistoryListFragment.TAG).commit();
        }
    }
}
