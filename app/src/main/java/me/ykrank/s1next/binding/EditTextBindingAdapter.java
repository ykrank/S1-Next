package me.ykrank.s1next.binding;

import android.databinding.BindingAdapter;
import android.widget.EditText;

public final class EditTextBindingAdapter {

    private EditTextBindingAdapter() {
    }

    @BindingAdapter("editable")
    public static void setHasProgress(EditText editText, Boolean editable) {
        editText.setEnabled(editable);
    }
}
