package io.github.pavelnajmon.stockanalyzer.scheduler;

import io.github.pavelnajmon.stockanalyzer.configuration.StockRefreshProperties;
import io.github.pavelnajmon.stockanalyzer.service.StockRefreshService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class StockDataRefreshSchedulerTest {

    private StockRefreshService stockRefreshService;
    private StockDataRefreshScheduler scheduler;

    @BeforeEach
    void setUp() {
        stockRefreshService = mock(StockRefreshService.class);

        StockRefreshProperties stockRefreshProperties = new StockRefreshProperties();
        stockRefreshProperties.setStockDataLimit(100);

        scheduler = new StockDataRefreshScheduler(
                stockRefreshService,
                stockRefreshProperties
        );
    }

    @Test
    void refreshMarketDays_shouldCallMarketSnapshotRefresh() {
        scheduler.refreshMarketDays();

        verify(stockRefreshService).refreshDailyMarketSnapshots();
        verifyNoMoreInteractions(stockRefreshService);
    }

    @Test
    void refreshStockData_shouldCallStockDataRefreshWithConfiguredLimit() {
        scheduler.refreshStockData();

        verify(stockRefreshService).refreshStockDataLimited(100);
        verifyNoMoreInteractions(stockRefreshService);
    }
}