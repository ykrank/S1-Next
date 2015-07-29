package cl.monsoon.s1next.viewmodel;

import android.databinding.ObservableField;

public final class TextAssetViewModel {

    public final ObservableField<String> filePath = new ObservableField<>();

    public TextAssetViewModel(String filePath) {
        this.filePath.set(filePath);
    }
}
