package io.github.pavelnajmon.stockanalyzer.service;

import io.github.pavelnajmon.stockanalyzer.model.dto.MarketDayDto;
import io.github.pavelnajmon.stockanalyzer.model.dto.StockDataDto;
import io.github.pavelnajmon.stockanalyzer.model.dto.response.StockDetailResponse;
import io.github.pavelnajmon.stockanalyzer.model.dto.response.StockSummaryResponse;
import io.github.pavelnajmon.stockanalyzer.model.entity.Stock;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public interface StockPersistenceService {
    @Transactional
    void saveStockWithMarketDays(StockDataDto stockDataDto, List<MarketDayDto> marketDays);

    boolean isTickerStored(String ticker);

    List<String> getAllTickers();

    Stock getStockById(Long stockId);

    StockSummaryResponse getStockSummary(Long stockId);

    List<StockSummaryResponse> getStockSummaries();

    StockDetailResponse getStockDetail(Long stockId);

    List<String> getTickersForStockDataRefresh(int limit);

    void addOrUpdateMarketDay(String ticker, MarketDayDto marketDayDto);

    void updateStockData(String ticker, StockDataDto stockDataDto);
}
