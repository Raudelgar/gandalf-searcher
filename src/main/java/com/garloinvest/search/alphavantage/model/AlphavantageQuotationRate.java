package com.garloinvest.search.alphavantage.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AlphavantageQuotationRate {

    @JsonProperty("1. From_Currency Code")
    private String codeFROM;
    @JsonProperty("3. To_Currency Code")
    private String codeTO;
    @JsonProperty("5. Exchange Rate")
    private String rate;
    @JsonProperty("6. Last Refreshed")
    private String lastRefreshed;
    @JsonProperty("7. Time Zone")
    private String timeZone;

    public AlphavantageQuotationRate() {
    }

    public String getCodeFROM() {
        return codeFROM;
    }

    public void setCodeFROM(String codeFROM) {
        this.codeFROM = codeFROM;
    }

    public String getCodeTO() {
        return codeTO;
    }

    public void setCodeTO(String codeTO) {
        this.codeTO = codeTO;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getLastRefreshed() {
        return lastRefreshed;
    }

    public void setLastRefreshed(String lastRefreshed) {
        this.lastRefreshed = lastRefreshed;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    @Override
    public String toString() {
        return "AlphavantageQuotationRate:" +
                "\n FROM Symbol Code='" + codeFROM + '\'' +
                "\n TO Symbol Code='" + codeTO + '\'' +
                "\n Rate='" + rate + '\'' +
                "\n Last Refreshed='" + lastRefreshed + '\'' +
                "\n Time Zone='" + timeZone + '\'';
    }
}
