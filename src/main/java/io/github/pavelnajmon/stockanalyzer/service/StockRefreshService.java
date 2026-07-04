package io.github.pavelnajmon.stockanalyzer.service;

import org.springframework.stereotype.Service;

@Service
public interface StockRefreshService {
    void refreshDailyMarketSnapshots();

    void refreshStockDataLimited(int limit);

    void refreshDailyData(int stockDataRefreshLimit);
}
