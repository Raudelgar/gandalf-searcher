package com.garloinvest.search.oanda.router;

import com.garloinvest.search.oanda.dto.candle.CandleInstrument;

import java.util.Map;

public interface OandaRouter {

    public Map<String, CandleInstrument> readOandaInstrumentCandleStickPerMinute();
}
