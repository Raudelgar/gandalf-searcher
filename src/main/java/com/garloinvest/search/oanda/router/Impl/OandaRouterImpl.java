package com.garloinvest.search.oanda.router.Impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.garloinvest.search.constants.GlobalConstants;
import com.garloinvest.search.oanda.dto.candle.CandleInstrument;
import com.garloinvest.search.oanda.router.OandaRouter;
import com.garloinvest.search.portfolio.FX;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class OandaRouterImpl implements OandaRouter {
    private static final Logger LOG = LoggerFactory.getLogger(OandaRouterImpl.class);

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private Environment environment;
    @Autowired
    private ObjectMapper objectMapper;
    private static final String DOMAIN = "oanda.fxpracticeapi.domain";
    private static final String INSTRUMENTS_PATH = "oanda.fxpracticeapi.restv20.instruments";
    private static final String CANDLES_PATH = "oanda.fxpracticeapi.restv20.instruments.candles";

//    @Scheduled(fixedRate = 1000)
    @Scheduled(cron = "0 0/1 * ? * MON-FRI")
    public void init() {
//        readOandaInstrumentCandleStickPerMinute();
        test();
    }

    @Override
    public Map<String, CandleInstrument> readOandaInstrumentCandleStickPerMinute() {
        return null;
    }

    public void test() {
       String domain = environment.getProperty(DOMAIN);
       String instrumentsPath = environment.getProperty(INSTRUMENTS_PATH);
       String candlesPath = environment.getProperty(CANDLES_PATH);

       String url = domain+instrumentsPath+FX.EUR_USD+candlesPath+GlobalConstants.oandaOneMinute;
       LOG.info("URL -> {}",url);
    }
}
