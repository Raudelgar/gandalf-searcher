package com.garloinvest.search.oanda.router;

import com.garloinvest.search.oanda.dto.candle.OandaInstrumentCandlestick;
import com.garloinvest.search.oanda.dto.price.OandaInstrumentPrice;

import java.time.LocalDateTime;
import java.util.Map;

public interface OandaRouter {

    public Map<String, Map<LocalDateTime, OandaInstrumentCandlestick>> readOandaInstrumentCandlestickPerMinute();
    public Map<String, OandaInstrumentPrice> readOandaInstrumentPrice();
}
