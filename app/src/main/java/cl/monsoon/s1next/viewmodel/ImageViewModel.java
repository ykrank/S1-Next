package cl.monsoon.s1next.viewmodel;

import android.databinding.ObservableField;

public final class ImageViewModel {

    public final ObservableField<String> url = new ObservableField<>();

    public ImageViewModel(String url) {
        this.url.set(url);
    }
}
