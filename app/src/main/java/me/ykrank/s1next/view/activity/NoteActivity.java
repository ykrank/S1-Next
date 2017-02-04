package me.ykrank.s1next.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import me.ykrank.s1next.R;
import me.ykrank.s1next.view.fragment.NoteFragment;
import me.ykrank.s1next.widget.track.event.page.PageEndEvent;
import me.ykrank.s1next.widget.track.event.page.PageStartEvent;

/**
 * Created by ykrank on 2017/1/5.
 */

public class NoteActivity extends BaseActivity {
    private Fragment fragment;

    public static void start(Context context) {
        Intent intent = new Intent(context, NoteActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_without_drawer);

        if (savedInstanceState == null) {
            fragment = NoteFragment.newInstance();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.frame_layout, fragment, NoteFragment.TAG)
                    .commit();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        trackAgent.post(new PageStartEvent(this, "消息列表-NoteActivity"));
    }

    @Override
    public void onPause() {
        trackAgent.post(new PageEndEvent(this, "消息列表-NoteActivity"));
        super.onPause();
    }
}
