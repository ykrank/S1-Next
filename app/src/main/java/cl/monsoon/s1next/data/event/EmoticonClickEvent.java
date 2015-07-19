package cl.monsoon.s1next.data.event;

public final class EmoticonClickEvent {

    private final String emoticonEntity;

    public EmoticonClickEvent(String emoticonEntity) {
        this.emoticonEntity = emoticonEntity;
    }

    public String getEmoticonEntity() {
        return emoticonEntity;
    }
}
