package io.github.pavelnajmon.stockanalyzer.service;

import io.github.pavelnajmon.stockanalyzer.mapper.MarketDayMapper;
import io.github.pavelnajmon.stockanalyzer.mapper.StockMapper;
import io.github.pavelnajmon.stockanalyzer.model.dto.MarketDayDto;
import io.github.pavelnajmon.stockanalyzer.model.dto.StockDataDto;
import io.github.pavelnajmon.stockanalyzer.model.entity.MarketDay;
import io.github.pavelnajmon.stockanalyzer.model.entity.Stock;
import io.github.pavelnajmon.stockanalyzer.repository.StockRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class StockPersistenceServiceImpl implements StockPersistenceService {

    private final StockRepository stockRepository;

    public StockPersistenceServiceImpl(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    @Override
    @Transactional
    public void saveStockWithMarketDays(StockDataDto stockDataDto, List<MarketDayDto> marketDays) {
        Stock stock = StockMapper.toEntity(stockDataDto);

        for (MarketDayDto marketDayDto : marketDays) {
            MarketDay marketDay = MarketDayMapper.toEntity(marketDayDto);
            stock.addMarketDay(marketDay);
        }

        stockRepository.save(stock);
    }
}
