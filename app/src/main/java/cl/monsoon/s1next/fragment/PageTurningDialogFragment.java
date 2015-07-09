package cl.monsoon.s1next.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;

import com.google.common.collect.Range;

import cl.monsoon.s1next.R;
import cl.monsoon.s1next.widget.InputFilterRange;

public abstract class PageTurningDialogFragment extends DialogFragment {

    public static final String TAG = PageTurningDialogFragment.class.getSimpleName();

    private static final String ARG_CURRENT_PAGE = "current_page";
    private static final String ARG_TOTAL_PAGES = "total_pages";

    /**
     * The serialization (saved instance state) Bundle key representing
     * the SeekBar's progress when page turning dialog is showing.
     */
    private static final String STATE_SEEKBAR_PROGRESS = "seekbar_progress";
    private int mSeekBarProgress = -1;

    public PageTurningDialogFragment() {
        // Every fragment must have an empty constructor, so it
        // can be instantiated when restoring its activity's state.
    }

    public PageTurningDialogFragment(int currentPage, int totalPages) {
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_CURRENT_PAGE, currentPage);
        bundle.putInt(ARG_TOTAL_PAGES, totalPages);
        setArguments(bundle);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        @SuppressWarnings("InflateParams") View view = getActivity().getLayoutInflater().inflate(
                R.layout.dialog_page_turning, null);
        if (mSeekBarProgress == -1) {
            mSeekBarProgress = getArguments().getInt(ARG_CURRENT_PAGE);
        }
        int totalPage = getArguments().getInt(ARG_TOTAL_PAGES);

        SeekBar seekBar = (SeekBar) view.findViewById(R.id.seek_bar);
        // SeekBar is zero-based!
        seekBar.setMax(totalPage - 1);
        seekBar.setProgress(mSeekBarProgress);

        EditText valueView = (EditText) view.findViewById(R.id.value);
        valueView.setText(String.valueOf(mSeekBarProgress + 1));
        valueView.setEms(String.valueOf(totalPage).length());
        // set EditText range filter
        valueView.setFilters(new InputFilter[]{new InputFilterRange(Range.closed(1, totalPage))});
        valueView.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    int value = Integer.parseInt(s.toString());
                    if (value - 1 != seekBar.getProgress()) {
                        seekBar.setProgress(value - 1);
                    }
                } catch (NumberFormatException ignored) {

                }
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mSeekBarProgress = progress;

                int value = -1;
                try {
                    value = Integer.parseInt(valueView.getText().toString());
                } catch (NumberFormatException ignored) {

                }

                if (progress + 1 != value) {
                    valueView.setText(String.valueOf(progress + 1));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.dialog_title_page_turning)
                .setView(view)
                .setPositiveButton(getText(R.string.dialog_button_text_go),
                        (dialog, which) -> {
                            if (!TextUtils.isEmpty(valueView.getText())) {
                                onPageTurning(seekBar.getProgress());
                            }
                        })
                .setNegativeButton(android.R.string.cancel, null)
                .create();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt(STATE_SEEKBAR_PROGRESS, mSeekBarProgress);
    }

    protected abstract void onPageTurning(int page);
}
