package cl.monsoon.s1next.viewmodel;

import android.databinding.BaseObservable;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableList;

public final class DropDownItemListViewModel {

    public ObservableList<CharSequence> dropDownItemList = new ObservableArrayList<>();

    private int selectedItemPosition;

    public int getSelectedItemPosition() {
        return selectedItemPosition;
    }

    /**
     * We do not call {@link BaseObservable#notifyChange()} in this method,
     * because we only need to notify change when {@link #dropDownItemList}
     * changes.
     */
    public void setSelectedItemPosition(int selectedItemPosition) {
        this.selectedItemPosition = selectedItemPosition;
    }
}
