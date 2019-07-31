package me.ykrank.s1next.data.api.model.collection;

import androidx.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Objects;

import java.util.List;

import me.ykrank.s1next.data.api.model.Account;
import me.ykrank.s1next.data.api.model.PmGroup;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class PmGroups extends Account {

    @JsonProperty("perpage")
    private int pmPerPage;

    @JsonProperty("page")
    private int page;

    @JsonProperty("count")
    private int total;

    @Nullable
    @JsonProperty("list")
    private List<PmGroup> pmGroupList;

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
    public List<PmGroup> getPmGroupList() {
        return pmGroupList;
    }

    public void setPmGroupList(@Nullable List<PmGroup> pmGroupList) {
        this.pmGroupList = pmGroupList;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public boolean hasNew() {
        if (pmGroupList != null && !pmGroupList.isEmpty()) {
            for (PmGroup pmGroup : pmGroupList) {
                if (pmGroup.isNew()) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        PmGroups pmGroups = (PmGroups) o;
        return pmPerPage == pmGroups.pmPerPage &&
                page == pmGroups.page &&
                total == pmGroups.total &&
                Objects.equal(pmGroupList, pmGroups.pmGroupList);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(super.hashCode(), pmPerPage, page, total, pmGroupList);
    }
}
