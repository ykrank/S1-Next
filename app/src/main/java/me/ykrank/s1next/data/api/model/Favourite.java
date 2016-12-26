package me.ykrank.s1next.data.api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Objects;

import org.apache.commons.lang3.StringEscapeUtils;

@SuppressWarnings("UnusedDeclaration")
@JsonIgnoreProperties(ignoreUnknown = true)
public final class Favourite {

    @JsonProperty("id")
    private String id;

    @JsonProperty("favid")
    private String favId;

    @JsonProperty("title")
    private String title;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        // unescape some basic XML entities
        this.title = StringEscapeUtils.unescapeXml(title);
    }

    public String getFavId() {
        return favId;
    }

    public void setFavId(String favId) {
        this.favId = favId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Favourite favourite = (Favourite) o;
        return Objects.equal(id, favourite.id) &&
                Objects.equal(favId, favourite.favId) &&
                Objects.equal(title, favourite.title);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id, favId, title);
    }
}
