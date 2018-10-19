package com.garloinvest.search.alphavantage.router;

import com.garloinvest.search.alphavantage.model.AlphavantageQuotationRate;
import com.garloinvest.search.alphavantage.model.AlphavantageQuotation;

import java.util.Map;

public interface AlphavantageRouter {

    public Map<String, AlphavantageQuotation> readAlphavantageStock_TimeSeriesIntraday();
    public Map<String, AlphavantageQuotation> readAlphavantageFX_FXIntraday();
    public Map<String, AlphavantageQuotationRate> readAlphavantageFX_CurrencyExchangeRate();
}
