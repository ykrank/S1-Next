package me.ykrank.s1next.data.api.model.wrapper;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import me.ykrank.s1next.data.api.model.collection.PmGroups;

@SuppressWarnings("UnusedDeclaration")
@JsonIgnoreProperties(ignoreUnknown = true)
public final class PmGroupsWrapper {

    @JsonProperty("Variables")
    private PmGroups pmGroups;

    public PmGroups getPmGroups() {
        return pmGroups;
    }

    public void setPmGroups(PmGroups pmGroups) {
        this.pmGroups = pmGroups;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PmGroupsWrapper pmGroupsWrapper = (PmGroupsWrapper) o;

        return pmGroups != null ? pmGroups.equals(pmGroupsWrapper.pmGroups) : pmGroupsWrapper.pmGroups == null;

    }

    @Override
    public int hashCode() {
        return pmGroups != null ? pmGroups.hashCode() : 0;
    }
}
