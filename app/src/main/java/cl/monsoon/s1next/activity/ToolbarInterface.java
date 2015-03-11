package cl.monsoon.s1next.activity;

import java.util.List;

public interface ToolbarInterface {

    interface OnDropDownItemSelectedListener {

        void onToolbarDropDownItemSelected(int position);
    }

    interface SpinnerCallback {

        /**
         * Sets up Toolbar's drop down items.
         */
        void setupToolbarDropDown(List<? extends CharSequence> dropDownItemList);
    }
}
