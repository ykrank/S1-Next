package me.ykrank.s1next.viewmodel;

import androidx.databinding.BaseObservable;
import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableList;

public final class DropDownItemListViewModel {

    public final ObservableList<CharSequence> dropDownItemList = new ObservableArrayList<>();

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
