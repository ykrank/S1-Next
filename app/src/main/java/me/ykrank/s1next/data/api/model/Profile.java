package me.ykrank.s1next.data.api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * Created by ykrank on 2017/1/8.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public final class Profile extends Account {

    public Profile(@JsonProperty("extcredits") JsonNode extCredits, @JsonProperty("space") JsonNode space) {

    }
}
