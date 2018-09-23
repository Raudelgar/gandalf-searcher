package com.garloinvest.search.alphavantage.router.Impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.garloinvest.search.alphavantage.dto.AlphavantageQuotation;
import com.garloinvest.search.alphavantage.dto.forex.AlphavantageQuoteFX;
import com.garloinvest.search.alphavantage.dto.stock.AlphavantageQuoteStock;
import com.garloinvest.search.alphavantage.router.AlphavantageRouter;
import com.garloinvest.search.alphavantage.util.WriteCsv;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

@Service
public class AlphavantageRouterImpl implements AlphavantageRouter{

    private static final Logger LOG = LoggerFactory.getLogger(AlphavantageRouterImpl.class);
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private Environment environment;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private WriteCsv wrtieCsv;
    private final String TIME_SERIES_INTADAY = "alphavantage.time_series_intraday";
    private final String CURRENCY_EXCHANGE_RATE = "alphavantage.currency_exchange_rate";

    @Override
//    @Scheduled(cron = "0 0/5 * ? * MON-FRI")
    @Scheduled(fixedRate = 1000)
    public Map<String, AlphavantageQuotation> readAlphavantageTimeSeriesIntraday() {
        LOG.info("Reading TIME_SERIES_INTRADAY");
        String url = environment.getProperty(TIME_SERIES_INTADAY);
        AlphavantageQuoteStock quote = restTemplate.getForObject(url, AlphavantageQuoteStock.class);
        Map<String, AlphavantageQuotation> quoteMap = new TreeMap<>();

        LOG.info("Symbol: {}",quote.getMetadata().getSymbol());
        LOG.info("Last Refreshed: {}",quote.getMetadata().getLastRefreshed());
        LOG.info("Interval: {}",quote.getMetadata().getInterval());

        try {
            JsonNode root = objectMapper.readTree(new URL(url));
            JsonNode node = root.path("Time Series ("+quote.getMetadata().getInterval()+")");
            Iterator<String> iterator = node.fieldNames();
            while(iterator.hasNext()) {
                String key = iterator.next();
                String jsonQuote = node.get(key).toString();
                AlphavantageQuotation quotation = new ObjectMapper().readValue(jsonQuote, AlphavantageQuotation.class);
                quoteMap.put(key,quotation);
            }
/*            for(Map.Entry<String, AlphavantageQuotation> entry : quoteMap.entrySet()) {
                LOG.info("STOCK: Date -> {}  Quote -> {}",entry.getKey(), entry.getValue());
            }*/
        } catch (IOException e) {
            e.printStackTrace();
            LOG.error("An error occurs reading TIME_SERIES_INTADAY from AlphavantageRouterImpl: {}",e.getMessage());
        }

//        wrtieCsv.savedAlphavantageTimeSeriesIntraday(quoteMap, quote.getMetadata().getLastRefreshed());
        return quoteMap;
    }

    @Override
    //    @Scheduled(cron = "0 0/5 * ? * MON-FRI")
    @Scheduled(fixedRate = 1000)
    public Map<String, AlphavantageQuotation> readAlphavantageCurrencyExchangeRate() {
        LOG.info("Reading CURRENCY_EXCHANGE_RATE");
        String url = environment.getProperty(CURRENCY_EXCHANGE_RATE);
        AlphavantageQuoteFX quote = restTemplate.getForObject(url, AlphavantageQuoteFX.class);
        Map<String, AlphavantageQuotation> quoteMap = new TreeMap<>();

        LOG.info("Symbol FROM: {}",quote.getMetadata().getSymbolFrom());
        LOG.info("Symbol TO: {}",quote.getMetadata().getSymbolTo());
        LOG.info("Last Refreshed: {}",quote.getMetadata().getLastRefreshed());
        LOG.info("Interval: {}",quote.getMetadata().getInterval());
        LOG.info("Time Zone: {}",quote.getMetadata().getTimeZone());

        try {
            JsonNode root = objectMapper.readTree(new URL(url));
            JsonNode node = root.path("Time Series FX ("+quote.getMetadata().getInterval()+")");
            Iterator<String> iterator = node.fieldNames();
            while(iterator.hasNext()) {
                String key = iterator.next();
                String jsonQuote = node.get(key).toString();
                AlphavantageQuotation quotation = new ObjectMapper().readValue(jsonQuote, AlphavantageQuotation.class);
                quoteMap.put(key,quotation);
            }
/*            for(Map.Entry<String, AlphavantageQuotation> entry : quoteMap.entrySet()) {
                LOG.info("FX: Date -> {}  Quote -> {}",entry.getKey(), entry.getValue());
            }*/
        } catch (IOException e) {
            e.printStackTrace();
            LOG.error("An error occurs reading CURRENCY_EXCHANGE_RATE from AlphavantageRouterImpl: {}",e.getMessage());
        }
        return quoteMap;
    }
}
