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
    private final MarketDayMapper marketDayMapper;
    private final StockMapper stockMapper;

    public StockPersistenceServiceImpl(StockRepository stockRepository, MarketDayMapper marketDayMapper, StockMapper stockMapper) {
        this.stockRepository = stockRepository;
        this.marketDayMapper = marketDayMapper;
        this.stockMapper = stockMapper;
    }

    @Override
    @Transactional
    public void saveStockWithMarketDays(StockDataDto stockDataDto, List<MarketDayDto> marketDays) {
        Stock stock = stockMapper.toEntity(stockDataDto);

        for (MarketDayDto marketDayDto : marketDays) {
            MarketDay marketDay = marketDayMapper.toEntity(marketDayDto);
            stock.addMarketDay(marketDay);
        }

        stockRepository.save(stock);
    }
}
