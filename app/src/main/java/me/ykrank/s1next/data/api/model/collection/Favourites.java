package me.ykrank.s1next.data.api.model.collection;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Objects;

import java.util.List;

import me.ykrank.s1next.data.api.model.Account;
import me.ykrank.s1next.data.api.model.Favourite;

@SuppressWarnings("UnusedDeclaration")
@JsonIgnoreProperties(ignoreUnknown = true)
public final class Favourites extends Account {

    @JsonProperty("perpage")
    private int favouritesPerPage;

    @JsonProperty("count")
    private int total;

    @JsonProperty("list")
    private List<Favourite> favouriteList;

    public int getFavouritesPerPage() {
        return favouritesPerPage;
    }

    public void setFavouritesPerPage(int favouritesPerPage) {
        this.favouritesPerPage = favouritesPerPage;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<Favourite> getFavouriteList() {
        return favouriteList;
    }

    public void setFavouriteList(List<Favourite> favouriteList) {
        this.favouriteList = favouriteList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Favourites that = (Favourites) o;
        return Objects.equal(favouritesPerPage, that.favouritesPerPage) &&
                Objects.equal(total, that.total) &&
                Objects.equal(favouriteList, that.favouriteList);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(super.hashCode(), favouritesPerPage, total, favouriteList);
    }
}
