package com.garloinvest.search.oanda.dto.candle;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CandleInstrument {

    @JsonProperty("instrument")
    private String instrument;
    @JsonProperty("granularity")
    private String granularity;
    private List<CandleData> candleData;

    public CandleInstrument() {
    }

    public String getInstrument() {
        return instrument;
    }

    public void setInstrument(String instrument) {
        this.instrument = instrument;
    }

    public String getGranularity() {
        return granularity;
    }

    public void setGranularity(String granularity) {
        this.granularity = granularity;
    }

    public List<CandleData> getCandleData() {
        return candleData;
    }

    public void setCandleData(List<CandleData> candleData) {
        this.candleData = candleData;
    }
}
