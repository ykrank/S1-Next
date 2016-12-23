package me.ykrank.s1next.data.event;

public final class BlackListAddEvent {

    private final int authorPostId;
    private final String authorPostName;
    private final String remark;
    private final boolean isAdd;

    public BlackListAddEvent(int quotePostId, String quotePostCount, String remark, boolean isHide) {
        this.authorPostId = quotePostId;
        this.authorPostName = quotePostCount;
        this.remark = remark;
        this.isAdd = isHide;
    }

    public int getAuthorPostId() {
        return authorPostId;
    }

    public String getAuthorPostName() {
        return authorPostName;
    }

    public String getRemark() {
        return remark;
    }

    public boolean isAdd() {
        return isAdd;
    }
}
