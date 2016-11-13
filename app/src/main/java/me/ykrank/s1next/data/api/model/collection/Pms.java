package me.ykrank.s1next.data.api.model.collection;

import android.support.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import me.ykrank.s1next.data.api.model.Account;
import me.ykrank.s1next.data.api.model.Pm;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class Pms extends Account {

    @JsonProperty("perpage")
    private int pmPerPage;

    @JsonProperty("page")
    private int page;

    @JsonProperty("count")
    private int total;

    @Nullable
    @JsonProperty("list")
    private List<Pm> pmList;

    public int getPmPerPage() {
        return pmPerPage;
    }

    public void setPmPerPage(int pmPerPage) {
        this.pmPerPage = pmPerPage;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    @Nullable
    public List<Pm> getPmList() {
        return pmList;
    }

    public void setPmList(@Nullable List<Pm> pmList) {
        this.pmList = pmList;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Pms pms = (Pms) o;

        if (pmPerPage != pms.pmPerPage) return false;
        if (page != pms.page) return false;
        if (total != pms.total) return false;
        return pmList.equals(pms.pmList);

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + pmPerPage;
        result = 31 * result + page;
        result = 31 * result + total;
        result = 31 * result + pmList.hashCode();
        return result;
    }
}
