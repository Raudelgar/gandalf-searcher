package com.garloinvest.searcher.alphavantage.dto.stock;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AlphavantageQuoteStock {

    @JsonProperty("Meta Data")
    private AlphavantageMetadataStock metadata;

    public AlphavantageQuoteStock() {
    }

    public AlphavantageMetadataStock getMetadata() {
        return metadata;
    }

    public void setMetadata(AlphavantageMetadataStock metadata) {
        this.metadata = metadata;
    }

    @Override
    public String toString() {
        return "AlphavantageQuoteStock{" +
                "\n" + metadata +
                '}';
    }
}
