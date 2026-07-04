package io.github.pavelnajmon.stockanalyzer.provider;

import io.github.pavelnajmon.stockanalyzer.model.dto.MarketDayDto;

import java.util.List;
import java.util.Optional;

public interface HistoricalPriceProvider {
    List<MarketDayDto> getStockHistoricalPrices(String ticker);

    Optional<MarketDayDto> getLatestMarketDay(String ticker);
}
