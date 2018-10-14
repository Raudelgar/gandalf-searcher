package com.garloinvest.search.oanda.model;

import com.oanda.v20.primitives.DateTime;

import java.math.BigDecimal;

public class OandaInstrumentPrice {

    private boolean isTradeable;
    private DateTime time;
    private BigDecimal sell; //Bid
    private Long liquiditySell;
    private BigDecimal buy; //Ask
    private Long liquidityBuy;

    public OandaInstrumentPrice() {
    }

    public boolean isTradeable() {
        return isTradeable;
    }

    public void setTradeable(boolean tradeable) {
        isTradeable = tradeable;
    }

    public DateTime getTime() {
        return time;
    }

    public void setTime(DateTime time) {
        this.time = time;
    }

    public BigDecimal getSell() {
        return sell;
    }

    public void setSell(BigDecimal sell) {
        this.sell = sell;
    }

    public Long getLiquiditySell() {
        return liquiditySell;
    }

    public void setLiquiditySell(Long liquiditySell) {
        this.liquiditySell = liquiditySell;
    }

    public BigDecimal getBuy() {
        return buy;
    }

    public void setBuy(BigDecimal buy) {
        this.buy = buy;
    }

    public Long getLiquidityBuy() {
        return liquidityBuy;
    }

    public void setLiquidityBuy(Long liquidityBuy) {
        this.liquidityBuy = liquidityBuy;
    }

    @Override
    public String toString() {
        return "OandaInstrumentPrice{" +
                "isTradeable=" + isTradeable +
                ", time=" + time +
                ", sell=" + sell +
                ", liquiditySell=" + liquiditySell +
                ", buy=" + buy +
                ", liquidityBuy=" + liquidityBuy +
                '}';
    }
}
