package com.garloinvest.searcher.oanda.model;

public class CandleTable {

    private String id;
    private String dateTimePrice;
    private String prevOpen;
    private String prevClose;
    private String currentOpen;
    private String currentClose;
    private String action;



    public CandleTable() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDateTimePrice() {
        return dateTimePrice;
    }

    public void setDateTimePrice(String dateTimePrice) {
        this.dateTimePrice = dateTimePrice;
    }

    public String getPrevOpen() {
        return prevOpen;
    }

    public void setPrevOpen(String prevOpen) {
        this.prevOpen = prevOpen;
    }

    public String getPrevClose() {
        return prevClose;
    }

    public void setPrevClose(String prevClose) {
        this.prevClose = prevClose;
    }

    public String getCurrentOpen() {
        return currentOpen;
    }

    public void setCurrentOpen(String currentOpen) {
        this.currentOpen = currentOpen;
    }

    public String getCurrentClose() {
        return currentClose;
    }

    public void setCurrentClose(String currentClose) {
        this.currentClose = currentClose;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
}
