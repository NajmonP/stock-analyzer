package io.github.pavelnajmon.stockanalyzer.scheduler;

import io.github.pavelnajmon.stockanalyzer.configuration.StockRefreshProperties;
import io.github.pavelnajmon.stockanalyzer.service.StockRefreshService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class StockDataRefreshScheduler {

    private final StockRefreshService stockRefreshService;
    private final StockRefreshProperties stockRefreshProperties;

    public StockDataRefreshScheduler(StockRefreshService stockRefreshService, StockRefreshProperties stockRefreshProperties) {
        this.stockRefreshService = stockRefreshService;
        this.stockRefreshProperties = stockRefreshProperties;
    }

    @Scheduled(cron = "${stock.refresh.market-days-cron}", zone = "${stock.refresh.zone}")
    public void refreshMarketDays() {
        stockRefreshService.refreshDailyMarketSnapshots();
    }

    @Scheduled(cron = "${stock.refresh.stock-data-cron}", zone = "${stock.refresh.zone}")
    public void refreshStockData() {
        stockRefreshService.refreshStockDataLimited(stockRefreshProperties.getStockDataLimit());
    }
}