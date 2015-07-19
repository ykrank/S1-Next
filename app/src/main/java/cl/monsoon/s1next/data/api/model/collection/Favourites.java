package cl.monsoon.s1next.data.api.model.collection;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import cl.monsoon.s1next.data.api.model.Account;
import cl.monsoon.s1next.data.api.model.Favourite;

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
}
