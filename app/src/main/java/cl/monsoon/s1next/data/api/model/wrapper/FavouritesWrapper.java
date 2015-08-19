package cl.monsoon.s1next.data.api.model.wrapper;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Objects;

import cl.monsoon.s1next.data.api.model.Result;
import cl.monsoon.s1next.data.api.model.collection.Favourites;

@SuppressWarnings("UnusedDeclaration")
@JsonIgnoreProperties(ignoreUnknown = true)
public final class FavouritesWrapper {

    @JsonProperty("Variables")
    private Favourites favourites;

    @JsonProperty("Message")
    private Result result;

    public Favourites getFavourites() {
        return favourites;
    }

    public void setFavourites(Favourites favourites) {
        this.favourites = favourites;
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FavouritesWrapper that = (FavouritesWrapper) o;
        return Objects.equal(favourites, that.favourites) &&
                Objects.equal(result, that.result);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(favourites, result);
    }
}
