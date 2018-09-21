package com.garloinvest.search.alphavantage.router.Impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.garloinvest.search.alphavantage.dto.AlphavantageQuotation;
import com.garloinvest.search.alphavantage.dto.AlphavantageQuote;
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
import java.util.HashMap;
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

    @Override
    @Scheduled(cron = "0 0/5 * ? * MON-FRI")
    public Map<String, AlphavantageQuotation> readAlphavantageTimeSeriesIntraday() {
        LOG.info("Reading TIME_SERIES_INTRADAY");
        String url = environment.getProperty(TIME_SERIES_INTADAY);
        AlphavantageQuote quote = restTemplate.getForObject(url, AlphavantageQuote.class);
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
        } catch (IOException e) {
            e.printStackTrace();
            LOG.error("An error occurs reading TIME_SERIES_INTADAY from AlphavantageRouterImpl: {}",e.getMessage());
        }

        wrtieCsv.savedAlphavantageTimeSeriesIntraday(quoteMap);
        return quoteMap;
    }
}
