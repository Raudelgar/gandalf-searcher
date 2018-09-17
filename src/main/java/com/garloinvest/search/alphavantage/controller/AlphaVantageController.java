package com.garloinvest.search.alphavantage.controller;

import com.garloinvest.search.alphavantage.dto.Quotation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;

@Controller
public class AlphaVantageController {

    private static final Logger LOG = LoggerFactory.getLogger(AlphaVantageController.class);

    @GetMapping
    public Map<String, Quotation> getSymbolIntraDayQuote() {
        LOG.info("Requesting Intra-Day Quotes");
       return null;
    }

}
