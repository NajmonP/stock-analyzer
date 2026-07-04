package io.github.pavelnajmon.stockanalyzer.service;

import io.github.pavelnajmon.stockanalyzer.model.dto.MarketDayDto;
import io.github.pavelnajmon.stockanalyzer.model.dto.StockDataDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public interface StockPersistenceService {
    @Transactional
    void saveStockWithMarketDays(StockDataDto stockDataDto, List<MarketDayDto> marketDays);

    List<String> getAllTickers();

    List<String> getTickersForStockDataRefresh(int limit);

    void addOrUpdateMarketDay(String ticker, MarketDayDto marketDayDto);

    void updateStockData(String ticker, StockDataDto stockDataDto);
}
