package me.ykrank.s1next.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import me.ykrank.s1next.R;
import me.ykrank.s1next.view.fragment.NoteFragment;

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
            fragment = NoteFragment.Companion.newInstance();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.frame_layout, fragment, NoteFragment.Companion.getTAG())
                    .commit();
        }
    }
}
