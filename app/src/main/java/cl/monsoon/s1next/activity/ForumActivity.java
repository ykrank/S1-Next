package cl.monsoon.s1next.activity;

import android.app.Fragment;
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
import cl.monsoon.s1next.util.ObjectUtil;

/**
 * This Activity has Spinner in ToolBar to switch between two different views.
 * Implement the required {@link android.widget.AdapterView.OnItemSelectedListener}
 * interface for Spinner to switch between views.
 */
public final class ForumActivity
        extends BaseActivity
        implements AdapterView.OnItemSelectedListener, ToolbarInterface.SpinnerInteractionCallback {

    /**
     * The serialization (saved instance state) Bundle key representing
     * the position of the selected spinner item.
     */
    private static final String STATE_SPINNER_SELECTED_POSITION = "selected_position";

    /**
     * Store the selected Spinner position after restore save instance.
     */
    private int mSelectedPosition = 0;

    private Spinner mSpinner;
    private View mSpinnerView;

    private ToolbarInterface.OnDropDownItemSelectedListener mOnToolbarDropDownItemSelectedListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        Fragment fragment;
        FragmentManager fragmentManager = getFragmentManager();
        if (savedInstanceState == null) {
            fragment = new ForumFragment();

            fragmentManager.beginTransaction()
                    .replace(R.id.frame_layout, fragment, ForumFragment.TAG).commit();
        } else {
            mSelectedPosition = savedInstanceState.getInt(STATE_SPINNER_SELECTED_POSITION);
            fragment = fragmentManager.findFragmentByTag(ForumFragment.TAG);
        }

        if (fragment instanceof ToolbarInterface.OnDropDownItemSelectedListener) {
            mOnToolbarDropDownItemSelectedListener = ObjectUtil.uncheckedCast(fragment);
        } else {
            throw
                    new ClassCastException(
                            fragment + " must implement mOnToolbarDropDownItemSelectedListener.");
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt(STATE_SPINNER_SELECTED_POSITION, mSelectedPosition);
    }

    @Override
    void setupGlobalToolbar() {
        super.setupGlobalToolbar();

        if (mSpinnerView != null) {
            mSpinnerView.setVisibility(View.GONE);
        }
    }

    @Override
    void restoreToolbar() {
        super.restoreToolbar();

        if (mSpinnerView != null) {
            mSpinnerView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Implement {@link android.widget.AdapterView.OnItemSelectedListener}.
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        mSelectedPosition = position;
        mOnToolbarDropDownItemSelectedListener.OnToolbarDropDownItemSelected(position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    /**
     * Implement {@link ToolbarInterface.SpinnerInteractionCallback}.
     */
    @Override
    public void setupToolbarDropDown(List<? extends CharSequence> dropDownItemList) {
        if (mSpinner == null) {
            setTitle(null);

            // add Spinner (drop down) to Toolbar
            LayoutInflater.from(this).inflate(R.layout.toolbar_spinner, getToolbar(), true);
            mSpinner = (Spinner) getToolbar().findViewById(R.id.spinner);

            // set Listener to switch between views
            mSpinner.setOnItemSelectedListener(this);

            // We disable clickable in Spinner
            // and let its parents LinearLayout to handle
            // click event in order to increase clickable area.
            mSpinnerView = getToolbar().findViewById(R.id.toolbar_layout);
            mSpinnerView.setOnClickListener(v -> mSpinner.performClick());
        }
        mSpinner.setAdapter(getSpinnerAdapter(dropDownItemList));
        // invalid index when user's login status has changed
        if (mSpinner.getAdapter().getCount() - 1 < mSelectedPosition) {
            mSpinner.setSelection(0);
        } else {
            mSpinner.setSelection(mSelectedPosition);
        }
    }

    private ArrayAdapter getSpinnerAdapter(List<? extends CharSequence> dropDownItemList) {
        List<CharSequence> list = new ArrayList<>();
        // the first drop-down item is "全部"
        // and other items fetched from S1
        list.add(getResources().getText(R.string.toolbar_spinner_drop_down_forum_first_item));
        list.addAll(dropDownItemList);

        ArrayAdapter arrayAdapter = new ArrayAdapter<>(
                this,
                R.layout.toolbar_spinner_item, list);
        arrayAdapter.setDropDownViewResource(R.layout.toolbar_spinner_dropdown_item);

        return arrayAdapter;
    }
}
