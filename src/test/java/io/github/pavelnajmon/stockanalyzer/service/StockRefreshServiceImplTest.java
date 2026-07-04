package io.github.pavelnajmon.stockanalyzer.service;

import io.github.pavelnajmon.stockanalyzer.model.dto.MarketDayDto;
import io.github.pavelnajmon.stockanalyzer.model.dto.StockDataDto;
import io.github.pavelnajmon.stockanalyzer.provider.HistoricalPriceProvider;
import io.github.pavelnajmon.stockanalyzer.provider.StockDataProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

class StockRefreshServiceImplTest {

    private StockPersistenceService stockPersistenceService;
    private HistoricalPriceProvider historicalPriceProvider;
    private StockDataProvider stockDataProvider;
    private StockRefreshServiceImpl stockRefreshService;

    @BeforeEach
    void setUp() {
        stockPersistenceService = mock(StockPersistenceService.class);
        historicalPriceProvider = mock(HistoricalPriceProvider.class);
        stockDataProvider = mock(StockDataProvider.class);

        stockRefreshService = new StockRefreshServiceImpl(
                stockPersistenceService,
                historicalPriceProvider,
                stockDataProvider
        );
    }

    @Test
    void refreshDailyMarketSnapshots_shouldRefreshLatestMarketDayForAllTickers() {
        // given
        MarketDayDto aaplMarketDay = mock(MarketDayDto.class);
        MarketDayDto msftMarketDay = mock(MarketDayDto.class);

        when(stockPersistenceService.getAllTickers())
                .thenReturn(List.of("AAPL", "MSFT"));

        when(historicalPriceProvider.getLatestMarketDay("AAPL"))
                .thenReturn(Optional.of(aaplMarketDay));

        when(historicalPriceProvider.getLatestMarketDay("MSFT"))
                .thenReturn(Optional.of(msftMarketDay));

        // when
        stockRefreshService.refreshDailyMarketSnapshots();

        // then
        verify(stockPersistenceService).getAllTickers();

        verify(historicalPriceProvider).getLatestMarketDay("AAPL");
        verify(historicalPriceProvider).getLatestMarketDay("MSFT");

        verify(stockPersistenceService).addOrUpdateMarketDay("AAPL", aaplMarketDay);
        verify(stockPersistenceService).addOrUpdateMarketDay("MSFT", msftMarketDay);
    }

    @Test
    void refreshDailyMarketSnapshots_shouldSkipTickerWhenLatestMarketDayIsEmpty() {
        // given
        when(stockPersistenceService.getAllTickers())
                .thenReturn(List.of("AAPL"));

        when(historicalPriceProvider.getLatestMarketDay("AAPL"))
                .thenReturn(Optional.empty());

        // when
        stockRefreshService.refreshDailyMarketSnapshots();

        // then
        verify(historicalPriceProvider).getLatestMarketDay("AAPL");
        verify(stockPersistenceService, never()).addOrUpdateMarketDay(eq("AAPL"), any());
    }

    @Test
    void refreshDailyMarketSnapshots_shouldContinueWhenOneTickerFails() {
        // given
        MarketDayDto msftMarketDay = mock(MarketDayDto.class);

        when(stockPersistenceService.getAllTickers())
                .thenReturn(List.of("AAPL", "MSFT"));

        when(historicalPriceProvider.getLatestMarketDay("AAPL"))
                .thenThrow(new RuntimeException("Yahoo error"));

        when(historicalPriceProvider.getLatestMarketDay("MSFT"))
                .thenReturn(Optional.of(msftMarketDay));

        // when
        stockRefreshService.refreshDailyMarketSnapshots();

        // then
        verify(historicalPriceProvider).getLatestMarketDay("AAPL");
        verify(historicalPriceProvider).getLatestMarketDay("MSFT");

        verify(stockPersistenceService, never()).addOrUpdateMarketDay(eq("AAPL"), any());
        verify(stockPersistenceService).addOrUpdateMarketDay("MSFT", msftMarketDay);
    }

    @Test
    void refreshStockDataLimited_shouldRefreshOnlyTickersReturnedByPersistenceService() {
        // given
        StockDataDto aaplData = mock(StockDataDto.class);
        StockDataDto msftData = mock(StockDataDto.class);

        when(stockPersistenceService.getTickersForStockDataRefresh(100))
                .thenReturn(List.of("AAPL", "MSFT"));

        when(stockDataProvider.getStockData("AAPL"))
                .thenReturn(aaplData);

        when(stockDataProvider.getStockData("MSFT"))
                .thenReturn(msftData);

        // when
        stockRefreshService.refreshStockDataLimited(100);

        // then
        verify(stockPersistenceService).getTickersForStockDataRefresh(100);

        verify(stockDataProvider).getStockData("AAPL");
        verify(stockDataProvider).getStockData("MSFT");

        verify(stockPersistenceService).updateStockData("AAPL", aaplData);
        verify(stockPersistenceService).updateStockData("MSFT", msftData);
    }

    @Test
    void refreshStockDataLimited_shouldContinueWhenOneTickerFails() {
        // given
        StockDataDto msftData = mock(StockDataDto.class);

        when(stockPersistenceService.getTickersForStockDataRefresh(100))
                .thenReturn(List.of("AAPL", "MSFT"));

        when(stockDataProvider.getStockData("AAPL"))
                .thenThrow(new RuntimeException("FMP error"));

        when(stockDataProvider.getStockData("MSFT"))
                .thenReturn(msftData);

        // when
        stockRefreshService.refreshStockDataLimited(100);

        // then
        verify(stockDataProvider).getStockData("AAPL");
        verify(stockDataProvider).getStockData("MSFT");

        verify(stockPersistenceService, never()).updateStockData(eq("AAPL"), any());
        verify(stockPersistenceService).updateStockData("MSFT", msftData);
    }
}