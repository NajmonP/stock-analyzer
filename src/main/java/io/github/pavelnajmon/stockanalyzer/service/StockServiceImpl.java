package io.github.pavelnajmon.stockanalyzer.service;

import io.github.pavelnajmon.stockanalyzer.exception.DuplicateException;
import io.github.pavelnajmon.stockanalyzer.exception.TickerBlankException;
import io.github.pavelnajmon.stockanalyzer.model.dto.MarketDayDto;
import io.github.pavelnajmon.stockanalyzer.model.dto.StockDataDto;
import io.github.pavelnajmon.stockanalyzer.model.enums.Attribute;
import io.github.pavelnajmon.stockanalyzer.provider.HistoricalPriceProvider;
import io.github.pavelnajmon.stockanalyzer.provider.StockDataProvider;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StockServiceImpl implements StockService {

    private final StockDataProvider stockDataProvider;
    private final HistoricalPriceProvider historicalPriceProvider;
    private final StockPersistenceService stockPersistenceService;

    public StockServiceImpl(StockDataProvider stockDataProvider, HistoricalPriceProvider historicalPriceProvider, StockPersistenceService stockPersistenceService) {
        this.stockDataProvider = stockDataProvider;
        this.historicalPriceProvider = historicalPriceProvider;
        this.stockPersistenceService = stockPersistenceService;
    }

    @Override
    public void addStock(String ticker) {
        String normalizedTicker = normalizeTicker(ticker);

        if(stockPersistenceService.isTickerStored(normalizedTicker)){
            throw new DuplicateException(Attribute.TICKER);
        }

        StockDataDto stockDataDto = stockDataProvider.getStockData(normalizedTicker);
        List<MarketDayDto> marketDays = historicalPriceProvider.getStockHistoricalPrices(normalizedTicker);

        stockPersistenceService.saveStockWithMarketDays(stockDataDto, marketDays);
    }

    private String normalizeTicker(String ticker) {
        if (ticker == null || ticker.isBlank()) {
            throw new TickerBlankException();
        }

        return ticker.trim().toUpperCase();
    }
}
