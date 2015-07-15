package cl.monsoon.s1next.viewmodel;

import android.databinding.ObservableBoolean;
import android.view.View;

public final class WebPageViewModel {

    public final ObservableBoolean finishedLoading = new ObservableBoolean();

    public int getWebViewVisibility() {
        return finishedLoading.get() ? View.VISIBLE : View.GONE;
    }

    public int getProgressBarVisibility() {
        return finishedLoading.get() ? View.GONE : View.VISIBLE;
    }
}
