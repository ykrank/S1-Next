package cl.monsoon.s1next.view.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

import cl.monsoon.s1next.R;
import cl.monsoon.s1next.view.fragment.ForumFragment;

/**
 * This Activity has Spinner in Toolbar to switch between different forum groups.
 */
public final class ForumActivity extends BaseActivity
        implements ToolbarInterface.SpinnerCallback,
        AdapterView.OnItemSelectedListener {

    /**
     * The serialization (saved instance state) Bundle key representing
     * the position of the selected spinner item.
     */
    private static final String STATE_SPINNER_SELECTED_POSITION = "selected_position";

    private Spinner mSpinner;

    /**
     * Stores the selected Spinner position.
     */
    private int mSelectedPosition = 0;

    private ToolbarInterface.OnDropDownItemSelectedListener mOnToolbarDropDownItemSelectedListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_base);

        Fragment fragment;
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (savedInstanceState == null) {
            fragment = new ForumFragment();

            fragmentManager.beginTransaction().replace(R.id.frame_layout, fragment,
                    ForumFragment.TAG).commit();
        } else {
            mSelectedPosition = savedInstanceState.getInt(STATE_SPINNER_SELECTED_POSITION);
            fragment = fragmentManager.findFragmentByTag(ForumFragment.TAG);
        }

        mOnToolbarDropDownItemSelectedListener = (ToolbarInterface.OnDropDownItemSelectedListener) fragment;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt(STATE_SPINNER_SELECTED_POSITION, mSelectedPosition);
    }

    /**
     * Implements {@link android.widget.AdapterView.OnItemSelectedListener}.
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        mSelectedPosition = position;
        mOnToolbarDropDownItemSelectedListener.onToolbarDropDownItemSelected(mSelectedPosition);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    /**
     * Implements {@link ToolbarInterface.SpinnerCallback}.
     */
    @Override
    public void setupToolbarDropDown(List<? extends CharSequence> dropDownItemList) {
        if (mSpinner == null) {
            setTitle(null);

            Toolbar toolbar = getToolbar();
            // add Spinner to Toolbar
            getLayoutInflater().inflate(R.layout.toolbar_spinner, toolbar, true);
            mSpinner = (Spinner) toolbar.findViewById(R.id.spinner);

            mSpinner.setOnItemSelectedListener(this);
            // let spinner's parent to handle clicking event in order
            // to increase clickable area.
            View spinnerContainer = toolbar.findViewById(R.id.spinner_container);
            spinnerContainer.setOnClickListener(v -> mSpinner.performClick());
        }

        mSpinner.setAdapter(getSpinnerAdapter(dropDownItemList));
        // invalid index may occurs when user's login status has changed
        if (mSpinner.getAdapter().getCount() - 1 < mSelectedPosition) {
            mSpinner.setSelection(0, false);
        } else {
            mSpinner.setSelection(mSelectedPosition, false);
        }
    }

    private BaseAdapter getSpinnerAdapter(List<? extends CharSequence> dropDownItemList) {
        // don't use dropDownItemList#add(int, E)
        // otherwise we will have multiple "全部"
        // if we invoke this method many times

        List<CharSequence> list = new ArrayList<>();
        // the first drop-down item is "全部"
        // and other items fetched from S1
        list.add(getResources().getString(R.string.toolbar_spinner_drop_down_all_forums_item_title));
        list.addAll(dropDownItemList);

        ArrayAdapter<CharSequence> arrayAdapter = new ArrayAdapter<>(this,
                R.layout.toolbar_spinner_item, list);
        arrayAdapter.setDropDownViewResource(R.layout.toolbar_spinner_dropdown_item);

        return arrayAdapter;
    }
}
