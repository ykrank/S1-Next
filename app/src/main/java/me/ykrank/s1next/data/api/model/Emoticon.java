package me.ykrank.s1next.data.api.model;

import android.util.Pair;

import com.github.ykrank.androidtools.guava.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Emoticon emoticon = (Emoticon) o;
        return Objects.equal(pair, emoticon.pair);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(pair);
    }
}
