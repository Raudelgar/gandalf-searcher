package com.garloinvest.search.alphavantage.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AlphavantageTimeSeries {

    private Map<String, AlphavantageQuotation> intervalMap;

    public AlphavantageTimeSeries() {
    }

    public Map<String, AlphavantageQuotation> getIntervalMap() {
        return intervalMap;
    }

    public void setIntervalMap(Map<String, AlphavantageQuotation> intervalMap) {
        this.intervalMap = intervalMap;
    }

    @Override
    public String toString() {
        return "AlphavantageTimeSeries:" +
                "intervalMap=" + intervalMap +
                '}';
    }

}
