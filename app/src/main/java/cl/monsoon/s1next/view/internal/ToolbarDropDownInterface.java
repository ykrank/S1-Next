package cl.monsoon.s1next.view.internal;

import java.util.List;

public interface ToolbarDropDownInterface {

    interface OnItemSelectedListener {

        void onToolbarDropDownItemSelected(int position);
    }

    interface Callback {

        void setupToolbarDropDown(List<? extends CharSequence> dropDownItemList);
    }
}
