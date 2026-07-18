package io.github.pavelnajmon.stockanalyzer.service;

import io.github.pavelnajmon.stockanalyzer.exception.EntityNotFoundException;
import io.github.pavelnajmon.stockanalyzer.exception.StockNotSavedException;
import io.github.pavelnajmon.stockanalyzer.mapper.MarketDayMapper;
import io.github.pavelnajmon.stockanalyzer.mapper.StockMapper;
import io.github.pavelnajmon.stockanalyzer.model.dto.MarketDayDto;
import io.github.pavelnajmon.stockanalyzer.model.dto.StockDataDto;
import io.github.pavelnajmon.stockanalyzer.model.dto.response.StockDetailResponse;
import io.github.pavelnajmon.stockanalyzer.model.dto.response.StockSummaryResponse;
import io.github.pavelnajmon.stockanalyzer.model.entity.MarketDay;
import io.github.pavelnajmon.stockanalyzer.model.entity.Stock;
import io.github.pavelnajmon.stockanalyzer.model.enums.EntityType;
import io.github.pavelnajmon.stockanalyzer.repository.MarketDayRepository;
import io.github.pavelnajmon.stockanalyzer.repository.StockRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Service
public class StockPersistenceServiceImpl implements StockPersistenceService {

    private final StockRepository stockRepository;
    private final MarketDayRepository marketDayRepository;
    private final MarketDayMapper marketDayMapper;
    private final StockMapper stockMapper;

    public StockPersistenceServiceImpl(
            StockRepository stockRepository,
            MarketDayRepository marketDayRepository,
            MarketDayMapper marketDayMapper,
            StockMapper stockMapper
    ) {
        this.stockRepository = stockRepository;
        this.marketDayRepository = marketDayRepository;
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

    @Override
    @Transactional(readOnly = true)
    public boolean isTickerStored(String ticker) {
        return stockRepository.existsByTicker(ticker);
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getAllTickers() {
        return stockRepository.findAllTickers();
    }

    @Override
    @Transactional(readOnly = true)
    public Stock getStockById(Long stockId){
        return stockRepository.findById(stockId).orElseThrow(() -> new EntityNotFoundException(EntityType.STOCK));
    }

    @Override
    @Transactional(readOnly = true)
    public StockSummaryResponse getStockSummary(Long stockId) {
        Stock stock = stockRepository.findById(stockId)
                .orElseThrow(() -> new EntityNotFoundException(EntityType.STOCK));

        BigDecimal lastClosePrice = marketDayRepository
                .findLastClosePriceByStockTicker(stock.getTicker())
                .orElseThrow(() -> new EntityNotFoundException(EntityType.STOCK));

        return stockMapper.toStockSummaryResponse(stock, lastClosePrice);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StockSummaryResponse> getStockSummaries() {
        List<Stock> stocks = stockRepository.findAllOrderByMarketCapitalizationDesc();

        return stocks.stream()
                .map(stock -> {
                    BigDecimal lastClosePrice = marketDayRepository.findLastClosePriceByStockTicker(stock.getTicker())
                            .orElseThrow(() -> new EntityNotFoundException(EntityType.STOCK));

                    return stockMapper.toStockSummaryResponse(stock, lastClosePrice);
                })
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public StockDetailResponse getStockDetail(Long stockId) {
        Stock stock = stockRepository.findById(stockId).orElseThrow(() -> new EntityNotFoundException(EntityType.STOCK));

        StockDataDto stockDataDto = stockMapper.toDto(stock);

        List<MarketDayDto> marketDayDtos = stock.getMarketDays()
                .stream()
                .map(marketDayMapper::toDto)
                .toList();

        return new StockDetailResponse(stockDataDto, marketDayDtos);
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getTickersForStockDataRefresh(int limit) {
        return stockRepository.findTickersForStockDataRefresh(PageRequest.of(0, limit));
    }

    @Override
    @Transactional
    public void addOrUpdateMarketDay(String ticker, MarketDayDto marketDayDto) {
        Stock stock = stockRepository.findByTicker(ticker)
                .orElseThrow(() -> new StockNotSavedException(ticker));

        marketDayRepository.findByStockTickerAndDate(ticker, marketDayDto.date())
                .ifPresentOrElse(
                        existingMarketDay -> marketDayMapper.updateEntityFromDto(marketDayDto, existingMarketDay),
                        () -> {
                            MarketDay newMarketDay = marketDayMapper.toEntity(marketDayDto);
                            stock.addMarketDay(newMarketDay);
                        }
                );
    }

    @Override
    @Transactional
    public void updateStockData(String ticker, StockDataDto stockDataDto) {
        Stock stock = stockRepository.findByTicker(ticker)
                .orElseThrow(() -> new StockNotSavedException(ticker));

        stockMapper.updateEntityFromDto(stockDataDto, stock);
        stock.setLastUpdatedAt(Instant.now());
    }
}
