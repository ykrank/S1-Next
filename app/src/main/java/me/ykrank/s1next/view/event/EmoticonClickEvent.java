package me.ykrank.s1next.view.event;

public final class EmoticonClickEvent {

    private final String emoticonEntity;

    public EmoticonClickEvent(String emoticonEntity) {
        this.emoticonEntity = emoticonEntity;
    }

    public String getEmoticonEntity() {
        return emoticonEntity;
    }
}
