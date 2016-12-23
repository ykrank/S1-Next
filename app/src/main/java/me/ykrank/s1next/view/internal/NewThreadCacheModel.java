package me.ykrank.s1next.view.internal;

import com.google.common.base.Objects;

public class NewThreadCacheModel {
    private int selectPosition;
    private String title;
    private String message;

    public int getSelectPosition() {
        return selectPosition;
    }

    public void setSelectPosition(int selectPosition) {
        this.selectPosition = selectPosition;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NewThreadCacheModel)) return false;
        NewThreadCacheModel that = (NewThreadCacheModel) o;
        return selectPosition == that.selectPosition &&
                Objects.equal(title, that.title) &&
                Objects.equal(message, that.message);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(selectPosition, title, message);
    }
}
