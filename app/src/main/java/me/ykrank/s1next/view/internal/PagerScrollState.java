package me.ykrank.s1next.view.internal;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.IntDef;

/**
 * Created by ykrank on 2017/1/18.
 * ScrollState when scroll to one page in one viewpager, and then scroll to one position in recycleView at this page
 */

public class PagerScrollState implements Parcelable{
    /**
     * 加载进度处于空闲状态
     */
    public static final int FREE = 0;
    /**
     * 加载进度处于滑动到指定页之前
     */
    public static final int BEFORE_SCROLL_PAGE = 1;
    /**
     * 加载进度处于滑动到指定位置之前
     */
    public static final int BEFORE_SCROLL_POSITION = 2;

    public PagerScrollState(){

    }

    @SuppressWarnings("WrongConstant")
    protected PagerScrollState(Parcel in) {
        state = in.readInt();
    }

    public static final Creator<PagerScrollState> CREATOR = new Creator<PagerScrollState>() {
        @Override
        public PagerScrollState createFromParcel(Parcel in) {
            return new PagerScrollState(in);
        }

        @Override
        public PagerScrollState[] newArray(int size) {
            return new PagerScrollState[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(state);
    }

    /**
     * 加载进度
     */
    @IntDef({FREE, BEFORE_SCROLL_PAGE, BEFORE_SCROLL_POSITION})
    public @interface ScrollState {
    }

    @ScrollState
    private int state = FREE;

    @ScrollState
    public int getState() {
        return state;
    }

    public void setState(@ScrollState int state) {
        this.state = state;
    }
}
