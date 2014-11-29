package cl.monsoon.s1next.widget;

import java.util.List;

public interface ToolbarSpinnerInteractionCallback {

    /**
     * Set up ToolBar's drop down items.
     */
    public void setupToolbarDropDown(List<? extends CharSequence> dropDownItem);
}
