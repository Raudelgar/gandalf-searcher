package com.garloinvest.search.alphavantage.dto.forex;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AlphavantageQuoteFX {

    @JsonProperty("Meta Data")
    private AlphavantageMetadataFX metadata;

    public AlphavantageQuoteFX() {
    }

    public AlphavantageMetadataFX getMetadata() {
        return metadata;
    }

    public void setMetadata(AlphavantageMetadataFX metadata) {
        this.metadata = metadata;
    }

    @Override
    public String toString() {
        return "AlphavantageQuoteFX{" +
                "metadata=" + metadata +
                '}';
    }
}
