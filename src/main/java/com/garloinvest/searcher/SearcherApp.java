package com.garloinvest.searcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SearcherApp {
    private static final Logger LOG = LoggerFactory.getLogger(SearcherApp.class);

    public static void main(String[] args) {
        SpringApplication.run(SearcherApp.class);
    }

}
