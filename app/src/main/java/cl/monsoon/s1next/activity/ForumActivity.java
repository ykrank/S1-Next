package cl.monsoon.s1next.activity;

import android.app.FragmentManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

import cl.monsoon.s1next.R;
import cl.monsoon.s1next.fragment.ForumFragment;

/**
 * This Activity has Spinner in ToolBar to
 * switch between two different views.
 * Implement the required {@link android.widget.AdapterView.OnItemSelectedListener}
 * interface for Spinner to switch between views.
 */
public final class ForumActivity extends AbsNavigationDrawerActivity
        implements AdapterView.OnItemSelectedListener, ForumFragment.OnToolbarSpinnerInteractionCallback {

    /**
     * The serialization (saved instance state) Bundle key representing
     * the position of the selected spinner item.
     */
    private static final String STATE_SPINNER_SELECTED_POSITION = "selected_position";

    /**
     * Store the selected Spinner position after restore save instance.
     * Only used in {@link #onItemSelected(android.widget.AdapterView, android.view.View, int, long)}.
     */
    private int mSelectedPosition = 0;

    private Spinner mSpinner;
    private View mToolBarLayout;

    private ForumFragment mForumFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FragmentManager fragmentManager = getFragmentManager();
        if (savedInstanceState == null) {
            mForumFragment = new ForumFragment();

            fragmentManager.beginTransaction()
                    .replace(R.id.frame_layout, mForumFragment, ForumFragment.TAG).commit();
        } else {
            mSelectedPosition = savedInstanceState.getInt(STATE_SPINNER_SELECTED_POSITION);
            mForumFragment = (ForumFragment) fragmentManager.findFragmentByTag(ForumFragment.TAG);
        }
    }

    @Override
    void showGlobalContext() {
        super.showGlobalContext();

        if (mToolBarLayout != null) {
            mToolBarLayout.setVisibility(View.GONE);
        }
    }

    @Override
    void restoreToolbar() {
        super.restoreToolbar();

        if (mToolBarLayout != null) {
            mToolBarLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt(STATE_SPINNER_SELECTED_POSITION, mSelectedPosition);
    }

    /**
     * Implement {@link android.widget.AdapterView.OnItemSelectedListener}.
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        mForumFragment.changeContent();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    /**
     * Implement {@link cl.monsoon.s1next.fragment.ForumFragment.OnToolbarSpinnerInteractionCallback}.
     */
    @Override
    public void setAdapterDataSet(List<? extends CharSequence> dropDownItem) {
        if (mSpinner == null) {
            setTitle(null);

            List<CharSequence> list = new ArrayList<>();
            // the first drop-down item is "全部"
            // and other items fetched from S1
            list.add(getResources().getText(R.string.toolbar_spinner_drop_down_forum_first_item));
            list.addAll(dropDownItem);

            ArrayAdapter arrayAdapter = new ArrayAdapter<>(this,
                    R.layout.toolbar_spinner_item, list);
            arrayAdapter.setDropDownViewResource(R.layout.toolbar_spinner_dropdown_item);

            // add Spinner to Toolbar
            LayoutInflater.from(this).inflate(R.layout.toolbar_spinner, mToolbar, true);
            mSpinner = (Spinner) mToolbar.findViewById(R.id.spinner);
            mSpinner.setAdapter(arrayAdapter);

            // set Listener to switch between views
            mSpinner.setOnItemSelectedListener(this);
            mSpinner.setSelection(mSelectedPosition);

            // We disable clickable in Spinner
            // and let its parents LinearLayout to handle
            // click event in order to increase clickable area.
            mToolBarLayout = mToolbar.findViewById(R.id.toolbar_layout);
            mToolBarLayout.setOnClickListener(v -> mSpinner.performClick());
        } else {
            mForumFragment.changeContent();
        }
    }

    @Override
    public int getItemPosition() {
        return mSpinner.getSelectedItemPosition();
    }
}
