package cl.monsoon.s1next.viewmodel;

import android.databinding.BaseObservable;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.IntDef;
import android.view.View;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public final class LoadingViewModel extends BaseObservable implements Parcelable {

    public static final Creator<LoadingViewModel> CREATOR = new Creator<LoadingViewModel>() {

        @Override
        public LoadingViewModel createFromParcel(Parcel source) {
            return new LoadingViewModel(source);
        }

        @Override
        public LoadingViewModel[] newArray(int i) {
            return new LoadingViewModel[i];
        }
    };

    public static final int LOADING_FINISH = 0;

    /**
     * We show circular indeterminate {@link android.widget.ProgressBar}
     * for the first time.
     */
    public static final int LOADING_FIRST_TIME = 1;

    public static final int LOADING_SWIPE_REFRESH = 2;

    public static final int LOADING_PULL_UP_TO_REFRESH = 3;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({
            LOADING_FINISH,
            LOADING_FIRST_TIME,
            LOADING_SWIPE_REFRESH,
            LOADING_PULL_UP_TO_REFRESH
    })
    private @interface LoadingDef {

    }

    private int loading;

    public LoadingViewModel() {

    }

    private LoadingViewModel(Parcel source) {
        loading = source.readInt();
    }

    @LoadingDef
    public int getLoading() {
        return loading;
    }

    public void setLoading(@LoadingDef int loading) {
        this.loading = loading;
        notifyChange();
    }

    public boolean isSwipeRefresh() {
        return loading == LOADING_SWIPE_REFRESH;
    }

    public boolean isPullToUpRefresh() {
        return loading == LOADING_PULL_UP_TO_REFRESH;
    }

    public int getProgressVisibility() {
        return loading == LOADING_FIRST_TIME ? View.VISIBLE : View.GONE;
    }

    public int getSwipeRefreshLayoutVisibility() {
        return loading == LOADING_FIRST_TIME ? View.INVISIBLE : View.VISIBLE;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(loading);
    }
}
