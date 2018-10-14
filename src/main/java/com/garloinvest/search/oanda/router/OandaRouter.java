package com.garloinvest.search.oanda.router;

import com.garloinvest.search.oanda.dto.candle.CandleInstrument;
import com.garloinvest.search.oanda.model.OandaInstrumentPrice;

import java.util.Map;

public interface OandaRouter {

    public Map<String, CandleInstrument> readOandaInstrumentCandleStickPerMinute();
    public Map<String, OandaInstrumentPrice> readOandaInstrumentPrice();
}
