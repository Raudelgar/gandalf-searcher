package com.garloinvest.search.alphavantage.router;

import com.garloinvest.search.alphavantage.dto.AlphavantageQuotation;

import java.util.Map;

public interface AlphavantageRouter {

    public Map<String, AlphavantageQuotation> readAlphavantageTimeSeriesIntraday();
}
