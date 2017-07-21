package me.ykrank.s1next.view.event;

public final class FavoriteRemoveEvent {
    private String favId;

    public FavoriteRemoveEvent(String favId) {
        this.favId = favId;
    }

    public String getFavId() {
        return favId;
    }
}
