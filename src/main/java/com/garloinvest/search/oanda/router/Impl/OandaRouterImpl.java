package com.garloinvest.search.oanda.router.Impl;

import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.garloinvest.search.connection.ConnectionOandaFXPractice;
import com.garloinvest.search.constants.GlobalConstants;
import com.garloinvest.search.oanda.dto.candle.CandleInstrument;
import com.garloinvest.search.oanda.model.OandaInstrumentPrice;
import com.garloinvest.search.oanda.router.OandaRouter;
import com.garloinvest.search.portfolio.FX;
import com.oanda.v20.Context;
import com.oanda.v20.ContextBuilder;
import com.oanda.v20.ExecuteException;
import com.oanda.v20.RequestException;
import com.oanda.v20.account.AccountID;
import com.oanda.v20.account.AccountSummary;
import com.oanda.v20.pricing.*;
import com.oanda.v20.pricing_common.Price;
import com.oanda.v20.pricing_common.PriceBucket;
import com.oanda.v20.primitives.DateTime;
import com.sun.prism.shader.Solid_TextureYV12_AlphaTest_Loader;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.*;

@Service
public class OandaRouterImpl implements OandaRouter {
    private static final Logger LOG = LoggerFactory.getLogger(OandaRouterImpl.class);

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private Environment environment;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ConnectionOandaFXPractice connection;
    private static final String DOMAIN = "oanda.fxpracticeapi.domain";
    private static final String INSTRUMENTS_PATH = "oanda.fxpracticeapi.restv20.instruments";
    private static final String CANDLES_PATH = "oanda.fxpracticeapi.restv20.instruments.candles";
    private static final String TOKEN = "oanda.fxTradePractice.token";

//    @Scheduled(cron = "0 0/1 * ? * MON-FRI")
    @Scheduled(fixedRate = 1000)
    public void init() {
//        readOandaInstrumentPrice();
        testreadOandaInstrumentPrice(readOandaInstrumentPrice());
    }

    private void testreadOandaInstrumentPrice(Map<String, OandaInstrumentPrice> map) {
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
    public Map<String, CandleInstrument> readOandaInstrumentCandleStickPerMinute() {
        return null;
    }

    @Override
    public Map<String, OandaInstrumentPrice> readOandaInstrumentPrice() {
        Map<String,OandaInstrumentPrice> bid_ask_price_map = new HashMap<>();
        Context context = connection.getConnectionFXPractice();
        List<String> instruments = new ArrayList<>(Arrays.asList(FX.EUR_USD.toString(),FX.EUR_JPY.toString(),FX.USD_JPY.toString()));
        PricingGetRequest request = new PricingGetRequest(new AccountID(environment.getProperty("oanda.fxTradePractice.accountId")),instruments);
        try {
            PricingGetResponse response = context.pricing.get(request);
            LOG.info("Response Executed at -> {}", response.getTime());
            for(ClientPrice clientPrice : response.getPrices()) {
                OandaInstrumentPrice priceNow = new OandaInstrumentPrice();
                priceNow.setTradeable(clientPrice.getTradeable());
                priceNow.setTime(clientPrice.getTime());
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
