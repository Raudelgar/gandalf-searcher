package com.garloinvest.search.alphavantage.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AlphavantageQuote {

    @JsonProperty("Meta Data")
    private AlphavantageMetadata metadata;
    @JsonProperty("Time Series (5min)")
    private AlphavantageTimeSeries timeSeries;

    public AlphavantageQuote() {
    }

    public AlphavantageMetadata getMetadata() {
        return metadata;
    }

    public void setMetadata(AlphavantageMetadata metadata) {
        this.metadata = metadata;
    }

    public AlphavantageTimeSeries getTimeSeries() {
        return timeSeries;
    }

    public void setTimeSeries(AlphavantageTimeSeries timeSeries) {
        this.timeSeries = timeSeries;
    }

    @Override
    public String toString() {
        return "AlphavantageQuote{" +
                "\n" + metadata +
                "\n" + timeSeries +
                '}';
    }
}
