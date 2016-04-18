package me.ykrank.s1next.viewmodel;

import android.databinding.BaseObservable;
import android.view.View;

public final class WebPageViewModel extends BaseObservable {

    private boolean finishedLoading;

    public void setFinishedLoading(boolean finishedLoading) {
        this.finishedLoading = finishedLoading;
        notifyChange();
    }

    public int getWebViewVisibility() {
        return finishedLoading ? View.VISIBLE : View.INVISIBLE;
    }

    public int getProgressBarVisibility() {
        return finishedLoading ? View.GONE : View.VISIBLE;
    }
}
