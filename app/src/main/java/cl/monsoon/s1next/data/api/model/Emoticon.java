package cl.monsoon.s1next.data.api.model;

import android.util.Pair;

public final class Emoticon {

    private final Pair<String, String> pair;

    public Emoticon(String imagePath, String entity) {
        pair = Pair.create(imagePath, entity);
    }

    public String getImagePath() {
        return pair.first;
    }

    public String getEntity() {
        return pair.second;
    }
}
