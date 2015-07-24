package cl.monsoon.s1next.viewmodel;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.databinding.ObservableInt;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.SeekBar;

import com.google.common.collect.Range;

import cl.monsoon.s1next.BR;
import cl.monsoon.s1next.widget.InputFilterRange;

public final class PageTurningViewModel extends BaseObservable {

    private final ObservableInt seekBarMax = new ObservableInt();
    public int seekBarProgress;

    public PageTurningViewModel(int seekBarMax, int seekBarProgress) {
        this.seekBarMax.set(seekBarMax);
        this.seekBarProgress = seekBarProgress;
    }

    public int getSeekBarMax() {
        return seekBarMax.get();
    }

    @Bindable
    public int getSeekBarProgress() {
        return seekBarProgress;
    }

    @Bindable
    public CharSequence getSeekBarProgressText() {
        // current page is zero-based
        return String.valueOf(seekBarProgress + 1);
    }

    public void setSeekBarProgress(int seekBarProgress) {
        this.seekBarProgress = seekBarProgress;
        notifyPropertyChanged(BR.seekBarProgress);
        notifyPropertyChanged(BR.seekBarProgressText);
    }

    public SeekBar.OnSeekBarChangeListener getOnSeekBarChangeListener() {
        return new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress != PageTurningViewModel.this.seekBarProgress) {
                    setSeekBarProgress(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        };
    }

    public InputFilter[] getFilters() {
        // SeekBar max is zero-based
        return new InputFilter[]{new InputFilterRange(Range.closed(1, seekBarMax.get() + 1))};
    }

    public TextWatcher getTextWatcher() {
        return new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String value = s.toString();
                if (!TextUtils.isEmpty(s)) {
                    int progress = Integer.parseInt(value) - 1;
                    if (progress != PageTurningViewModel.this.seekBarProgress) {
                        setSeekBarProgress(progress);
                    }
                }
            }
        };
    }
}
