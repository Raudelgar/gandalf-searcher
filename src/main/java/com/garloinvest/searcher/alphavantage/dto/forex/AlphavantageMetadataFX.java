package com.garloinvest.searcher.alphavantage.dto.forex;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AlphavantageMetadataFX {

    @JsonProperty("2. From Symbol")
    private String symbolFrom;
    @JsonProperty("3. To Symbol")
    private String symbolTo;
    @JsonProperty("4. Last Refreshed")
    private String lastRefreshed;
    @JsonProperty("5. Interval")
    private String interval;
    @JsonProperty("7. Time Zone")
    private String timeZone;

    public AlphavantageMetadataFX() {
    }

    public String getSymbolFrom() {
        return symbolFrom;
    }

    public void setSymbolFrom(String symbolFrom) {
        this.symbolFrom = symbolFrom;
    }

    public String getSymbolTo() {
        return symbolTo;
    }

    public void setSymbolTo(String symbolTo) {
        this.symbolTo = symbolTo;
    }

    public String getLastRefreshed() {
        return lastRefreshed;
    }

    public void setLastRefreshed(String lastRefreshed) {
        this.lastRefreshed = lastRefreshed;
    }

    public String getInterval() {
        return interval;
    }

    public void setInterval(String interval) {
        this.interval = interval;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    @Override
    public String toString() {
        return "AlphavantageMetadataFX: " +
                "\n symbolFrom='" + symbolFrom + '\'' +
                "\n symbolTo='" + symbolTo + '\'' +
                "\n lastRefreshed='" + lastRefreshed + '\'' +
                "\n interval='" + interval + '\'' +
                "\n timeZone='" + timeZone + '\'';
    }
}
