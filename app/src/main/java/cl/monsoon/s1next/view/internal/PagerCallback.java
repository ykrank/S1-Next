package cl.monsoon.s1next.view.internal;

public interface PagerCallback {

    /**
     * A callback to set actual total pages
     * which used for {@link android.support.v4.view.PagerAdapter}。
     */
    void setTotalPage(int totalPage);
}
