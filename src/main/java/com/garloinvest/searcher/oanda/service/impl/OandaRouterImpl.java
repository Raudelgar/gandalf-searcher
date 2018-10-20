package com.garloinvest.searcher.oanda.service.impl;

import com.garloinvest.searcher.oanda.connection.OandaConnectionFXPractice;
import com.garloinvest.searcher.oanda.dto.candle.OandaInstrumentCandlestick;
import com.garloinvest.searcher.oanda.dto.price.OandaInstrumentPrice;
import com.garloinvest.searcher.oanda.service.OandaRouter;
import com.garloinvest.searcher.oanda.util.DateUtil;
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
        return bid_ask_price_map;
    }
}
