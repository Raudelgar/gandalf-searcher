package com.garloinvest.search.oanda.util;

import com.garloinvest.search.oanda.dto.candle.OandaInstrumentCandlestick;
import com.garloinvest.search.oanda.dto.price.OandaInstrumentPrice;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * This Util Class will perform part of the Rule and Risk Service logic.
 * The candlestick data will be read every minute (Open and Close).
 * The current price data will be read every second (Bid(Sell) and Ask(Buy))
 * Condition to executed a Limit Buy Order:
 *  - if the candle is completed(True). Note*: the last candle is always false, just take in count the prev candle
 *  - current Open >= prev Close
 *  - prev Open < prev Close
 *  - Set Limit Buy Order = current Open + 2pts (5th digit after the point)
 *  - Saved in a constant the current spread (Ask - Bid) => CP
 *  - Saved in a constant the current Bid => CS
 *  Conditions to executed a Limit Sell Order (Closed):
 *  - Calculate the different between the constant Bid(CS) minus the current Bid(S) = New Spread(NP)
 *  - Calculate the Risk (Z) taking the new spread (NP) minus the constant spread(CP)
 *  - If Risk(Z) is between 5 and 10, place the limit sell at 2ptos less than the current Bid
 */
@Repository
public class OandaWriteCsv {
    private static final Logger LOG = LoggerFactory.getLogger(OandaWriteCsv.class);
    private static final String DELIMITER = ",";
    private static final String NEW_LINE = "\n";
    private static final String HEADER = "DateTimePrice,PrevOpen,PrevClose,CurrentOpen,CurrentClose,SellPrice,BuyPrice,Action";
    private boolean isBuyExecuted = false;
    private int lines = 0;
    private String DateTimePrice;
    private String prevOpen;
    private String prevClose;
    private String currentOpen;
    private String currentClose;
    private String sellPrice;
    private String buyPrice;
    private String action;



    public void compareLastTwoCandlestick(Map<String, Map<LocalDateTime, OandaInstrumentCandlestick>> instrument_candle_map) {
        if(null == instrument_candle_map || instrument_candle_map.isEmpty()) {
            LOG.error("Candlestick is empty or Null");
            return;
        }
        LOG.info("Reading Candlestick");
        for(Map.Entry<String, Map<LocalDateTime, OandaInstrumentCandlestick>> entryInstrument : instrument_candle_map.entrySet()) {
            TreeMap<LocalDateTime, OandaInstrumentCandlestick> candleMap = (TreeMap<LocalDateTime, OandaInstrumentCandlestick>) entryInstrument.getValue();
            LocalDateTime lastTime = candleMap.lastKey();
            LocalDateTime prevTime = lastTime.minusMinutes(1);
            OandaInstrumentCandlestick prevCandle = candleMap.get(prevTime);
            OandaInstrumentCandlestick lastCandle = candleMap.get(lastTime);
            if(!prevCandle.isComplete()) {
                break;
            }
            //Verifying if the prevCandle was up and if the lastCandle is going up respect the prevCandle
            if(prevCandle.getOpen().compareTo(prevCandle.getClose()) < 0) {
                if(lastCandle.getOpen().compareTo(prevCandle.getClose()) >= 0) {
                    isBuyExecuted = true;
                    prevClose = String.valueOf(prevCandle.getClose());
                    prevOpen = String.valueOf(prevCandle.getOpen());
                    currentClose = String.valueOf(lastCandle.getClose());
                    currentOpen = String.valueOf(lastCandle.getOpen());
                    executedLimitBuy(lastCandle.getTime());
                }
            }else if(!isBuyExecuted) {
                break;
            }else {
                executedClosedTrade();
            }
        }
    }

    private void executedLimitBuy(LocalDateTime time) {
        String lastDate = time.toString();
        //Checking first if the 100 lines are completed
        if(lines == 100) {
            lines = 0;
        }
        //Creating a new File
        if(lines == 0) {
            File file = new File("Quote"+lastDate+".csv");
        }
        PrintWriter printWriter = null;
        try {
            printWriter = new PrintWriter(new FileWriter("Quote"+lastDate+".csv"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void executedClosedTrade() {
    }

    public void checkCurrentInstrumentPrice(Map<String, OandaInstrumentPrice> bid_ask_price_map) {
        if(!isBuyExecuted) {
            return;
        }
        if(null == bid_ask_price_map || bid_ask_price_map.isEmpty()) {
            LOG.error("Candlestick is empty or Null");
            return;
        }
        LOG.info("Reading Instrument Price");
        for(Map.Entry<String, OandaInstrumentPrice> entry : bid_ask_price_map.entrySet()) {
            System.out.println("Instrument ->"+entry.getKey());
            OandaInstrumentPrice price = entry.getValue();
            System.out.println("Time ->"+price.getTime());
            System.out.println("isTradeable ->"+price.isTradeable());
            System.out.println("Buy ->"+price.getBuy());
            System.out.println("LiquidityBuy ->"+price.getLiquidityBuy());
            System.out.println("Sell ->"+price.getSell());
            System.out.println("LiquiditySell ->"+price.getLiquiditySell());
            System.out.println("------------------------------");
        }
    }
}
