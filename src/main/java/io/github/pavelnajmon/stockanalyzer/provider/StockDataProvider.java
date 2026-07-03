package io.github.pavelnajmon.stockanalyzer.provider;

import io.github.pavelnajmon.stockanalyzer.model.dto.StockDataDto;

public interface StockDataProvider {
    public StockDataDto getStockData(String ticker);
}
