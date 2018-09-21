package com.garloinvest.search.alphavantage.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AlphavantageQuote {

    @JsonProperty("Meta Data")
    private AlphavantageMetadata metadata;

    public AlphavantageQuote() {
    }

    public AlphavantageMetadata getMetadata() {
        return metadata;
    }

    public void setMetadata(AlphavantageMetadata metadata) {
        this.metadata = metadata;
    }

    @Override
    public String toString() {
        return "AlphavantageQuote{" +
                "\n" + metadata +
                '}';
    }
}
