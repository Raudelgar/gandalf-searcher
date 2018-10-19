package com.garloinvest.search.oanda.router.Impl;

import com.garloinvest.search.oanda.connection.OandaConnectionFXPractice;
import com.garloinvest.search.oanda.dto.candle.OandaInstrumentCandlestick;
import com.garloinvest.search.oanda.dto.price.OandaInstrumentPrice;
import com.garloinvest.search.oanda.router.OandaRouter;
import com.garloinvest.search.oanda.util.DateUtil;
import com.garloinvest.search.oanda.util.OandaWriteCsv;
import com.garloinvest.search.portfolio.FX;
import com.oanda.v20.Context;
import com.oanda.v20.ExecuteException;
import com.oanda.v20.RequestException;
import com.oanda.v20.account.AccountID;
import com.oanda.v20.instrument.Candlestick;
import com.oanda.v20.instrument.CandlestickGranularity;
import com.oanda.v20.instrument.InstrumentCandlesRequest;
import com.oanda.v20.instrument.InstrumentCandlesResponse;
import com.oanda.v20.pricing.*;
import com.oanda.v20.pricing_common.PriceBucket;
import com.oanda.v20.primitives.InstrumentName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class OandaRouterImpl implements OandaRouter {
    private static final Logger LOG = LoggerFactory.getLogger(OandaRouterImpl.class);

    @Autowired
    private Environment environment;
    @Autowired
    private OandaConnectionFXPractice connection;
    @Autowired
    private OandaWriteCsv writeCsv;

//    @Scheduled(cron = "0 0/1 * ? * MON-FRI")
    @Scheduled(fixedRate = 1000)
    public void callInstrumentPrice() {
//        List<String> instruments = new ArrayList<>(Arrays.asList(FX.EUR_USD.toString(),FX.EUR_JPY.toString(),FX.USD_JPY.toString()));
        List<String> instruments = new ArrayList<>(Arrays.asList(FX.EUR_USD.toString()));
        readOandaInstrumentPrice(instruments);
//        testreadOandaInstrumentPrice(readOandaInstrumentPrice(instruments));
//        testreadOandaInstrumentCandlestick(readOandaInstrumentCandlestickPerMinute(instrumentName));
    }

    @Scheduled(fixedRate = 60000)
    public void callCandlestickEvryMinute() {
        String instrumentName = FX.EUR_USD.toString();
        readOandaInstrumentCandlestickPerMinute(instrumentName);
    }

    public void testreadOandaInstrumentCandlestick(Map<String, Map<LocalDateTime, OandaInstrumentCandlestick>> instrumentMap) {
        for(Map.Entry<String, Map<LocalDateTime, OandaInstrumentCandlestick>> entryInstrument : instrumentMap.entrySet()) {
            Map<LocalDateTime, OandaInstrumentCandlestick> candleMap = entryInstrument.getValue();
            System.out.println("How Many Candles: ->"+candleMap.size());
            System.out.println("Instrument ->"+entryInstrument.getKey());
            for(Map.Entry<LocalDateTime, OandaInstrumentCandlestick> entryCandle : candleMap.entrySet()) {
                OandaInstrumentCandlestick candlestick = entryCandle.getValue();
                System.out.println("        Time ->"+candlestick.getTime());
                System.out.println("        isCompleted ->"+candlestick.isComplete());
                System.out.println("        Volume: "+candlestick.getVolume());
                System.out.println("        Open: "+candlestick.getOpen());
                System.out.println("        Close: "+candlestick.getClose());
                System.out.println("        High: "+candlestick.getHigh());
                System.out.println("        Low: "+candlestick.getLow());
                System.out.println("------------------------------");
            }
        }
    }

    public void testreadOandaInstrumentPrice(Map<String, OandaInstrumentPrice> map) {
        for(Map.Entry<String, OandaInstrumentPrice> entry : map.entrySet()) {
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

    @Override
    public Map<String, Map<LocalDateTime, OandaInstrumentCandlestick>> readOandaInstrumentCandlestickPerMinute(String instrumentName) {
        Map<String, Map<LocalDateTime, OandaInstrumentCandlestick>> instrument_candle_map = new HashMap<>();
        Map<LocalDateTime, OandaInstrumentCandlestick> candlestickMap = new TreeMap<>();
        Context context = connection.getConnectionFXPractice();
        InstrumentCandlesRequest request = new InstrumentCandlesRequest(new InstrumentName(instrumentName));
        request.setGranularity(CandlestickGranularity.M1);

        try {
            InstrumentCandlesResponse response = context.instrument.candles(request);
            LOG.info("Response Executed for Instrument -> {} at every -> {}", response.getInstrument(), response.getGranularity().toString());
            for(Candlestick candlestick: response.getCandles()) {
                OandaInstrumentCandlestick candlestickNow = new OandaInstrumentCandlestick();
                candlestickNow.setTime(DateUtil.convertFromOandaDateTimeToJavaLocalDateTime(candlestick.getTime()));
                candlestickNow.setComplete(candlestick.getComplete());
                candlestickNow.setVolume(candlestick.getVolume());
                candlestickNow.setOpen(candlestick.getMid().getO().bigDecimalValue());
                candlestickNow.setClose(candlestick.getMid().getC().bigDecimalValue());
                candlestickNow.setHigh(candlestick.getMid().getH().bigDecimalValue());
                candlestickNow.setLow(candlestick.getMid().getL().bigDecimalValue());

                candlestickMap.put(candlestickNow.getTime(),candlestickNow);
                instrument_candle_map.put(response.getInstrument().toString(),candlestickMap);
            }
        } catch (RequestException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ExecuteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        writeCsv.compareLastTwoCandlestick(instrument_candle_map);
        return instrument_candle_map;
    }

    @Override
    public Map<String, OandaInstrumentPrice> readOandaInstrumentPrice(List<String> instruments) {
        Map<String,OandaInstrumentPrice> bid_ask_price_map = new HashMap<>();
        Context context = connection.getConnectionFXPractice();
        PricingGetRequest request = new PricingGetRequest(new AccountID(environment.getProperty("oanda.fxTradePractice.accountId")),instruments);
        try {
            PricingGetResponse response = context.pricing.get(request);
            LOG.info("Response Executed at -> {}", response.getTime());
            for(ClientPrice clientPrice : response.getPrices()) {
                OandaInstrumentPrice priceNow = new OandaInstrumentPrice();
                priceNow.setTradeable(clientPrice.getTradeable());
                priceNow.setTime(DateUtil.convertFromOandaDateTimeToJavaLocalDateTime(clientPrice.getTime()));
                for(PriceBucket priceBucket : clientPrice.getBids()) {
                    priceNow.setSell(priceBucket.getPrice().bigDecimalValue());
                    priceNow.setLiquiditySell(priceBucket.getLiquidity());
                }
                for(PriceBucket priceBucket : clientPrice.getAsks()) {
                    priceNow.setBuy(priceBucket.getPrice().bigDecimalValue());
                    priceNow.setLiquidityBuy(priceBucket.getLiquidity());
                }

                bid_ask_price_map.put(clientPrice.getInstrument().toString(),priceNow);
            }
        } catch (RequestException e) {
            e.printStackTrace();
            LOG.error("Error with Pricing Request -> {}", e.getErrorMessage());
        } catch (ExecuteException e) {
            e.printStackTrace();
            LOG.error("Error with Execution -> {}", e.getMessage());
        }
        writeCsv.checkCurrentInstrumentPrice(bid_ask_price_map);
        return bid_ask_price_map;
    }
}
