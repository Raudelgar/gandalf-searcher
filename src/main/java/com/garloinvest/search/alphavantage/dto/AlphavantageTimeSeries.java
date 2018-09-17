package com.garloinvest.search.alphavantage.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AlphavantageTimeSeries {

    private Map<String, Quotation> intervalMap;

    public AlphavantageTimeSeries() {
    }

    public Map<String, Quotation> getIntervalMap() {
        return intervalMap;
    }

    public void setIntervalMap(Map<String, Quotation> intervalMap) {
        this.intervalMap = intervalMap;
    }

    @Override
    public String toString() {
        return "AlphavantageTimeSeries:" +
                "intervalMap=" + intervalMap +
                '}';
    }

 /*   private String intervalMapPrint() {
        StringBuilder result = new StringBuilder();
        for(Map.Entry<String,Quotation> entry : intervalMap.entrySet()) {
            result.append(entry.getKey()+":");
            result.append("\n "+entry.getValue().getOpen());
        }
        return result.toString();
    }*/
}
