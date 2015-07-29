package cl.monsoon.s1next.binding;

import android.databinding.BindingAdapter;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.widget.EditText;

public final class EditTextBindingAdapter {

    @BindingAdapter("filters")
    public static void setFilters(EditText editText, InputFilter[] inputFilters) {
        editText.setFilters(inputFilters);
    }

    @BindingAdapter("addTextChangedListener")
    public static void addTextChangedListener(EditText editText, TextWatcher textWatcher) {
        editText.addTextChangedListener(textWatcher);
    }
}
