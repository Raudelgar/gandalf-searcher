package com.garloinvest.search.oanda.util;

import com.garloinvest.search.oanda.dto.candle.OandaInstrumentCandlestick;
import com.garloinvest.search.oanda.dto.price.OandaInstrumentPrice;
import com.garloinvest.search.oanda.model.CandleTable;
import com.garloinvest.search.oanda.model.InstrumentPriceTable;
import com.garloinvest.search.oanda.router.Impl.OandaRouterImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Properties;
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
    private Properties properties;
    private static final String DELIMITER = ",";
    private static final String NEW_LINE = "\n";
    private static final String HEADER = "ID,Instrument,DateTimePrice,PrevOpen,PrevClose,CurrentOpen,CurrentClose,SellPrice,BuyPrice,Action";
    private boolean isBuyExecuted = false;
    private int lines = 0;
    private int idFile = 0;
    private int id = 0;
    private String instrument;
    private String dateTimePrice;
    private String prevOpen;
    private String prevClose;
    private String currentOpen;
    private String currentClose;
    private String sellPrice;
    private String buyPrice;
    private String action;

    @Autowired
    private OandaRouterImpl router;


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
                    router.testreadOandaInstrumentCandlestick(instrument_candle_map);
                    isBuyExecuted = true;
                    id ++;
                    prevClose = String.valueOf(prevCandle.getClose());
                    prevOpen = String.valueOf(prevCandle.getOpen());
                    currentClose = String.valueOf(lastCandle.getClose());
                    currentOpen = String.valueOf(lastCandle.getOpen());
                    dateTimePrice = lastCandle.getTime().toString();
                    action = "BUY";
                    writeToCandleFile(id,prevClose,prevOpen,currentClose,currentOpen,dateTimePrice,action);
                    executedLimitBuy();
                }
            }else if(!isBuyExecuted) {
                break;
            }else {
                executedClosedTrade();
            }
        }
    }

    private void writeToCandleFile(int id, String prevClose, String prevOpen, String currentClose, String currentOpen, String dateTimePrice, String action) {
        properties = new Properties();
        OutputStream out = null;

        try {
            out = new FileOutputStream("candleTable.properties");
            properties.setProperty("id", String.valueOf(id));
            properties.setProperty("prevClose",prevClose);
            properties.setProperty("prevOpen",prevOpen);
            properties.setProperty("currentClose",currentClose);
            properties.setProperty("currentOpen",currentOpen);
            properties.setProperty("dateTimePrice",dateTimePrice);
            properties.setProperty("action",action);
            properties.store(out,null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(null != out) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void executedLimitBuy() {
        //Checking first if the 100 lines are completed
        if(lines >= 100) {
            lines = 0;
        }
        //Creating a new File
        if(lines == 0) {
            idFile ++;
            appendToNewFile(idFile);
        }else {
            appendToFile(idFile);
        }
    }

    private void appendToNewFile(int idFile) {

        CandleTable candleTable = readCandleTable();
        InstrumentPriceTable instrumentPriceTable = readInstrumentPriceTable();

        PrintWriter printWriter = null;
        try {
            printWriter = new PrintWriter(new FileWriter("Trade"+idFile+".csv"));
            StringBuilder sb = new StringBuilder();
            sb.append(HEADER);
            sb.append(NEW_LINE);
            sb.append(candleTable.getId());
            sb.append(DELIMITER);
            sb.append(instrumentPriceTable.getInstrument());
            sb.append(DELIMITER);
            sb.append(candleTable.getDateTimePrice());
            sb.append(DELIMITER);
            sb.append(candleTable.getPrevOpen());
            sb.append(DELIMITER);
            sb.append(candleTable.getPrevClose());
            sb.append(DELIMITER);
            sb.append(candleTable.getCurrentOpen());
            sb.append(DELIMITER);
            sb.append(candleTable.getCurrentClose());
            sb.append(DELIMITER);
            sb.append(instrumentPriceTable.getSellPrice());
            sb.append(DELIMITER);
            sb.append(instrumentPriceTable.getBuyPrice());
            sb.append(DELIMITER);
            sb.append(candleTable.getAction());
            printWriter.write(sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            printWriter.close();
        }
    }

    private InstrumentPriceTable readInstrumentPriceTable() {
        properties = new Properties();
        InputStream in = null;
        InstrumentPriceTable instrumentPriceTable = new InstrumentPriceTable();
        try {
            in = new FileInputStream("instrumentPriceTable.properties");
            properties.load(in);

            instrumentPriceTable.setInstrument(properties.getProperty("instrument"));
            instrumentPriceTable.setBuyPrice(properties.getProperty("buyPrice"));
            instrumentPriceTable.setSellPrice(properties.getProperty("sellPrice"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(null != in) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return instrumentPriceTable;
    }

    private CandleTable readCandleTable() {
        properties = new Properties();
        InputStream in = null;
        CandleTable candleTable = new CandleTable();
        try {
            in = new FileInputStream("candleTable.properties");
            properties.load(in);

            candleTable.setId(properties.getProperty("id"));
            candleTable.setDateTimePrice(properties.getProperty("dateTimePrice"));
            candleTable.setPrevOpen(properties.getProperty("prevOpen"));
            candleTable.setPrevClose(properties.getProperty("prevClose"));
            candleTable.setCurrentOpen(properties.getProperty("currentOpen"));
            candleTable.setCurrentClose(properties.getProperty("currentClose"));
            candleTable.setAction(properties.getProperty("action"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(null != in) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return candleTable;
    }

    private void appendToFile(int idFile) {
        CandleTable candleTable = readCandleTable();
        InstrumentPriceTable instrumentPriceTable = readInstrumentPriceTable();

        PrintWriter printWriter = null;
        try {
            printWriter = new PrintWriter(new FileWriter("Trade"+idFile+".csv"));
            StringBuilder sb = new StringBuilder();
            sb.append(NEW_LINE);
            sb.append(candleTable.getId());
            sb.append(DELIMITER);
            sb.append(instrumentPriceTable.getInstrument());
            sb.append(DELIMITER);
            sb.append(candleTable.getDateTimePrice());
            sb.append(DELIMITER);
            sb.append(candleTable.getPrevOpen());
            sb.append(DELIMITER);
            sb.append(candleTable.getPrevClose());
            sb.append(DELIMITER);
            sb.append(candleTable.getCurrentOpen());
            sb.append(DELIMITER);
            sb.append(candleTable.getCurrentClose());
            sb.append(DELIMITER);
            sb.append(instrumentPriceTable.getBuyPrice());
            sb.append(DELIMITER);
            sb.append(instrumentPriceTable.getSellPrice());
            sb.append(DELIMITER);
            sb.append(candleTable.getAction());
            printWriter.write(sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            printWriter.close();
        }
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
            instrument = entry.getKey();
            OandaInstrumentPrice price = entry.getValue();
            buyPrice = String.valueOf(price.getBuy());
            sellPrice = String.valueOf(price.getSell());
            writeToInstrumentFile(instrument,buyPrice,sellPrice);
            savedSellPriceAndSpread(price.getBuy(),price.getSell());
            checkRisk(price.getSell());
        }
    }

    private void writeToInstrumentFile(String instrument, String buyPrice, String sellPrice) {
        properties = new Properties();
        OutputStream out = null;

        try {
            out = new FileOutputStream("instrumentPriceTable.properties");
            properties.setProperty("instrument", instrument);
            properties.setProperty("buyPrice",buyPrice);
            properties.setProperty("sellPrice",sellPrice);
            properties.store(out,null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(null != out) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void savedSellPriceAndSpread(BigDecimal buyPrice, BigDecimal sellPrice) {
        int ask = buyPrice.multiply(BigDecimal.valueOf(100000)).intValue();
        int bid = sellPrice.multiply(BigDecimal.valueOf(100000)).intValue();
        int spread = ask - bid;
        properties = new Properties();
        OutputStream out = null;

        try {
            out = new FileOutputStream("instrument_constant.properties");
            properties.setProperty("sellPrice", sellPrice.toString());
            properties.setProperty("spread",String.valueOf(spread));
            properties.store(out,null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(null != out) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void executedClosedTrade() {
        if(!isBuyExecuted) {
            return;
        }else {
            isBuyExecuted = false;
            action = "SELL";
            LOG.info("****************############### CLOSE TRADE ---> SELL ############********************");
            appendToFile(idFile);
        }
    }

    private void checkRisk(BigDecimal sellPrice) {
        int newBid = sellPrice.multiply(BigDecimal.valueOf(100000)).intValue();
        properties = new Properties();
        InputStream in = null;
        try {
            in = new FileInputStream("instrument_constant.properties");
            properties.load(in);
            BigDecimal bid = new BigDecimal(properties.getProperty("sellPrice"));
            int prevBid = bid.multiply(BigDecimal.valueOf(100000)).intValue();
            int prevSpread = Integer.parseInt(properties.getProperty("spread"));
            if(prevBid - newBid > 0) {
                int newSpread = prevBid - newBid;
                int risk = newSpread - prevSpread;
                if(risk >= 7) {
                    executedClosedTrade();
                }
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(null != in) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
