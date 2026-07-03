package io.github.pavelnajmon.stockanalyzer.provider;

import io.github.pavelnajmon.stockanalyzer.model.dto.MarketDayDto;

import java.util.List;

public interface HistoricalPriceProvider {
    public List<MarketDayDto> getStockHistoricalPrices(String ticker);
}
