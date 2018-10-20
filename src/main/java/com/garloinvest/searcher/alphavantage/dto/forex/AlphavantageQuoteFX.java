package com.garloinvest.searcher.alphavantage.dto.forex;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.garloinvest.searcher.alphavantage.model.AlphavantageQuotationRate;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AlphavantageQuoteFX {

    @JsonProperty("Meta Data")
    private AlphavantageMetadataFX metadata;
    @JsonProperty("Realtime Currency Exchange Rate")
    private AlphavantageQuotationRate exchangeRate;

    public AlphavantageQuoteFX() {
    }

    public AlphavantageMetadataFX getMetadata() {
        return metadata;
    }

    public void setMetadata(AlphavantageMetadataFX metadata) {
        this.metadata = metadata;
    }

    public AlphavantageQuotationRate getExchangeRate() {
        return exchangeRate;
    }

    public void setExchangeRate(AlphavantageQuotationRate exchangeRate) {
        this.exchangeRate = exchangeRate;
    }

    @Override
    public String toString() {
        return "AlphavantageQuoteFX{" +
                "metadata=" + metadata +
                ", exchangeRate=" + exchangeRate +
                '}';
    }
}
