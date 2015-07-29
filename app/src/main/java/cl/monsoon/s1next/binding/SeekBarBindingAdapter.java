package cl.monsoon.s1next.binding;

import android.databinding.BindingAdapter;
import android.widget.SeekBar;

public final class SeekBarBindingAdapter {

    @BindingAdapter("onSeekBarChangeListener")
    public static void setOnSeekBarChangeListener(SeekBar seekBar, SeekBar.OnSeekBarChangeListener seekBarChangeListener) {
        seekBar.setOnSeekBarChangeListener(seekBarChangeListener);
    }
}
