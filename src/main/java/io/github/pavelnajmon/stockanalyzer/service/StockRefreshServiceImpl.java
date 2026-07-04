package io.github.pavelnajmon.stockanalyzer.service;

import io.github.pavelnajmon.stockanalyzer.model.dto.MarketDayDto;
import io.github.pavelnajmon.stockanalyzer.model.dto.StockDataDto;
import io.github.pavelnajmon.stockanalyzer.provider.HistoricalPriceProvider;
import io.github.pavelnajmon.stockanalyzer.provider.StockDataProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StockRefreshServiceImpl implements StockRefreshService {

    private static final Logger log = LoggerFactory.getLogger(StockRefreshServiceImpl.class);

    private final StockPersistenceService stockPersistenceService;
    private final HistoricalPriceProvider historicalPriceProvider;
    private final StockDataProvider stockDataProvider;

    public StockRefreshServiceImpl(
            StockPersistenceService stockPersistenceService,
            HistoricalPriceProvider historicalPriceProvider,
            StockDataProvider stockDataProvider
    ) {
        this.stockPersistenceService = stockPersistenceService;
        this.historicalPriceProvider = historicalPriceProvider;
        this.stockDataProvider = stockDataProvider;
    }

    @Override
    public void refreshDailyMarketSnapshots() {
        List<String> tickers = stockPersistenceService.getAllTickers();

        log.info("Starting daily market snapshot refresh for {} stocks.", tickers.size());

        for (String ticker : tickers) {
            try {
                Optional<MarketDayDto> latestMarketDay = historicalPriceProvider.getLatestMarketDay(ticker);

                if (latestMarketDay.isEmpty()) {
                    log.warn("No latest market day found for ticker {}.", ticker);
                    continue;
                }

                stockPersistenceService.addOrUpdateMarketDay(ticker, latestMarketDay.get());

                log.info("Daily market snapshot refreshed for ticker {}.", ticker);
            } catch (Exception exception) {
                log.error("Failed to refresh daily market snapshot for ticker {}.", ticker, exception);
            }
        }

        log.info("Daily market snapshot refresh finished.");
    }

    @Override
    public void refreshStockDataLimited(int limit) {
        List<String> tickers = stockPersistenceService.getTickersForStockDataRefresh(limit);

        log.info("Starting stock data refresh for {} stocks. Limit: {}.", tickers.size(), limit);

        for (String ticker : tickers) {
            try {
                StockDataDto stockDataDto = stockDataProvider.getStockData(ticker);
                stockPersistenceService.updateStockData(ticker, stockDataDto);

                log.info("Stock data refreshed for ticker {}.", ticker);
            } catch (Exception exception) {
                log.error("Failed to refresh stock data for ticker {}.", ticker, exception);
            }
        }

        log.info("Stock data refresh finished.");
    }

    @Override
    public void refreshDailyData(int stockDataRefreshLimit) {
        refreshDailyMarketSnapshots();
        refreshStockDataLimited(stockDataRefreshLimit);
    }
}