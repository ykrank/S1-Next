package cl.monsoon.s1next.viewmodel;

import android.databinding.ObservableField;

public final class ImageViewModel {

    public final ObservableField<CharSequence> url = new ObservableField<>();

    public ImageViewModel(CharSequence url) {
        this.url.set(url);
    }
}
