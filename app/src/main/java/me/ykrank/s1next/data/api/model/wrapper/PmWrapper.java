package me.ykrank.s1next.data.api.model.wrapper;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import me.ykrank.s1next.data.api.model.collection.Pms;

@SuppressWarnings("UnusedDeclaration")
@JsonIgnoreProperties(ignoreUnknown = true)
public final class PmWrapper {

    @JsonProperty("Variables")
    private Pms pms;

    public Pms getPms() {
        return pms;
    }

    public void setPms(Pms pms) {
        this.pms = pms;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PmWrapper pmWrapper = (PmWrapper) o;

        return pms != null ? pms.equals(pmWrapper.pms) : pmWrapper.pms == null;

    }

    @Override
    public int hashCode() {
        return pms != null ? pms.hashCode() : 0;
    }
}
