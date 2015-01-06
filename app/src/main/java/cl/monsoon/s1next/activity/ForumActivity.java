package cl.monsoon.s1next.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.List;

import cl.monsoon.s1next.R;
import cl.monsoon.s1next.fragment.ForumFragment;
import cl.monsoon.s1next.util.ObjectUtil;

/**
 * This Activity has Spinner in ToolBar to switch between different forum groups.
 */
public final class ForumActivity extends BaseActivity {

    private ToolbarInterface.OnDropDownItemSelectedListener mOnToolbarDropDownItemSelectedListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        enableWindowTranslucentStatus();

        Fragment fragment;
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (savedInstanceState == null) {
            fragment = new ForumFragment();

            fragmentManager.beginTransaction()
                    .replace(R.id.frame_layout, fragment, ForumFragment.TAG).commit();
        } else {
            fragment = fragmentManager.findFragmentByTag(ForumFragment.TAG);
        }

        mOnToolbarDropDownItemSelectedListener =
                ObjectUtil.cast(fragment, ToolbarInterface.OnDropDownItemSelectedListener.class);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        super.onItemSelected(parent, view, position, id);

        mOnToolbarDropDownItemSelectedListener.onToolbarDropDownItemSelected(position);
    }

    @Override
    ArrayAdapter getSpinnerAdapter(List<? extends CharSequence> dropDownItemList) {
        // don't use dropDownItemList#add(int, E)
        // otherwise we will have multiple "全部"
        // if we invoke this method many times
        List<CharSequence> list = new ArrayList<>(dropDownItemList);
        // the first drop-down item is "全部"
        // and other items fetched from S1
        list.add(0, getResources().getString(R.string.toolbar_spinner_drop_down_forum_first_item));

        return super.getSpinnerAdapter(list);
    }
}
