package me.ykrank.s1next.binding;

import android.content.Context;
import androidx.databinding.BindingAdapter;
import android.os.Build;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

import me.ykrank.s1next.R;
import me.ykrank.s1next.view.adapter.ArrayAdapterCompat;

public final class SpinnerBindingAdapter {

    private SpinnerBindingAdapter() {
    }

    @BindingAdapter({"dropDownItemList", "selectedItemPosition"})
    public static void setForumGroupNameList(Spinner spinner, List<CharSequence> dropDownItemList, int selectedItemPosition) {
        spinner.setAdapter(getSpinnerAdapter(spinner, dropDownItemList));
        // invalid position may occurs when user's login status has changed
        if (spinner.getAdapter().getCount() - 1 < selectedItemPosition) {
            spinner.setSelection(0, false);
        } else {
            spinner.setSelection(selectedItemPosition, false);
        }
    }

    private static BaseAdapter getSpinnerAdapter(Spinner spinner, List<CharSequence> dropDownItemList) {
        // don't use dropDownItemList#add(int, E)
        // otherwise we will have multiple "全部"
        // if we call this method many times

        List<CharSequence> list = new ArrayList<>();
        // the first drop-down item is "全部"
        // and other items fetched from S1
        list.add(spinner.getContext().getString(
                R.string.toolbar_spinner_drop_down_all_forums_item_title));
        list.addAll(dropDownItemList);

        ArrayAdapter<CharSequence> arrayAdapter;
        Context context = spinner.getContext();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            arrayAdapter = new ArrayAdapter<>(context, R.layout.toolbar_spinner_item, list);
        } else {
            arrayAdapter = new ArrayAdapterCompat<>(context, R.layout.toolbar_spinner_item, list);
        }
        arrayAdapter.setDropDownViewResource(R.layout.toolbar_spinner_dropdown_item);

        return arrayAdapter;
    }
}
