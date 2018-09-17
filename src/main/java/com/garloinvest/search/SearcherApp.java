package com.garloinvest.search;

import com.garloinvest.search.alphavantage.dto.AlphavantageQuote;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class SearcherApp {
    private static final Logger LOG = LoggerFactory.getLogger(SearcherApp.class);

    public static void main(String[] args) {
        SpringApplication.run(SearcherApp.class);
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder) {
        return restTemplateBuilder.build();
    }

    @Bean
    public CommandLineRunner run(RestTemplate restTemplate) {
        LOG.info("#############################################");
        LOG.info("#######   Running Searcher Service    #######");
        LOG.info("#############################################");
        String url = "https://www.alphavantage.co/query?function=TIME_SERIES_INTRADAY&symbol=MSFT&interval=5min&apikey=demo";
        return args -> {
            ResponseEntity<AlphavantageQuote> quote = restTemplate.getForEntity(url, AlphavantageQuote.class);
            LOG.info("********* AlphavantageQuote:\n"+quote.toString());
        };
    }
}
