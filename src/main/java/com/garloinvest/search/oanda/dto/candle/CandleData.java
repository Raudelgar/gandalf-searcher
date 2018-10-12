package com.garloinvest.search.oanda.dto.candle;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.garloinvest.search.oanda.model.OandaOHLC;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CandleData {

    @JsonProperty("complete")
    private boolean complete;
    @JsonProperty("volume")
    private String volume;
    @JsonProperty("time")
    private String time;
    @JsonProperty("mid")
    private OandaOHLC mid;

    public CandleData() {
    }

    public boolean isComplete() {
        return complete;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }

    public String getVolume() {
        return volume;
    }

    public void setVolume(String volume) {
        this.volume = volume;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public OandaOHLC getMid() {
        return mid;
    }

    public void setMid(OandaOHLC mid) {
        this.mid = mid;
    }

    @Override
    public String toString() {
        return "CandleData{" +
                "complete=" + complete +
                ", volume='" + volume + '\'' +
                ", time='" + time + '\'' +
                ", mid=" + mid +
                '}';
    }
}
