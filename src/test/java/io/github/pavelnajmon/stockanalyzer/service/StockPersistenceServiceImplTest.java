package io.github.pavelnajmon.stockanalyzer.service;

import io.github.pavelnajmon.stockanalyzer.mapper.MarketDayMapper;
import io.github.pavelnajmon.stockanalyzer.mapper.StockMapper;
import io.github.pavelnajmon.stockanalyzer.model.dto.MarketDayDto;
import io.github.pavelnajmon.stockanalyzer.model.dto.StockDataDto;
import io.github.pavelnajmon.stockanalyzer.model.entity.MarketDay;
import io.github.pavelnajmon.stockanalyzer.model.entity.Stock;
import io.github.pavelnajmon.stockanalyzer.repository.MarketDayRepository;
import io.github.pavelnajmon.stockanalyzer.repository.StockRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StockPersistenceServiceImplTest {

    @Mock
    private StockRepository stockRepository;

    @Mock
    private MarketDayRepository marketDayRepository;

    @Mock
    private MarketDayMapper marketDayMapper;

    @Mock
    private StockMapper stockMapper;


    private StockPersistenceServiceImpl stockPersistenceService;

    @BeforeEach
    void setUp() {
        stockPersistenceService = new StockPersistenceServiceImpl(
                stockRepository,
                marketDayRepository,
                marketDayMapper,
                stockMapper
        );
    }

    @Test
    void saveStockWithMarketDays_shouldMapStockAndMarketDaysAndSaveStock() {
        // given
        StockDataDto stockDataDto = mock(StockDataDto.class);

        MarketDayDto marketDayDto1 = mock(MarketDayDto.class);
        MarketDayDto marketDayDto2 = mock(MarketDayDto.class);
        List<MarketDayDto> marketDayDtos = List.of(marketDayDto1, marketDayDto2);

        Stock stock = mock(Stock.class);
        MarketDay marketDay1 = mock(MarketDay.class);
        MarketDay marketDay2 = mock(MarketDay.class);

        when(stockMapper.toEntity(stockDataDto)).thenReturn(stock);
        when(marketDayMapper.toEntity(marketDayDto1)).thenReturn(marketDay1);
        when(marketDayMapper.toEntity(marketDayDto2)).thenReturn(marketDay2);

        // when
        stockPersistenceService.saveStockWithMarketDays(stockDataDto, marketDayDtos);

        // then
        verify(stockMapper).toEntity(stockDataDto);

        verify(marketDayMapper).toEntity(marketDayDto1);
        verify(marketDayMapper).toEntity(marketDayDto2);

        verify(stock).addMarketDay(marketDay1);
        verify(stock).addMarketDay(marketDay2);

        verify(stockRepository).save(stock);
    }

    @Test
    void saveStockWithMarketDays_shouldSaveStockWhenMarketDaysAreEmpty() {
        // given
        StockDataDto stockDataDto = mock(StockDataDto.class);
        Stock stock = mock(Stock.class);

        when(stockMapper.toEntity(stockDataDto)).thenReturn(stock);

        // when
        stockPersistenceService.saveStockWithMarketDays(stockDataDto, List.of());

        // then
        verify(stockMapper).toEntity(stockDataDto);
        verifyNoInteractions(marketDayMapper);
        verify(stock, never()).addMarketDay(any());
        verify(stockRepository).save(stock);
    }

    @Test
    void saveStockWithMarketDays_shouldNotSaveStockWhenStockMappingFails() {
        // given
        StockDataDto stockDataDto = mock(StockDataDto.class);

        when(stockMapper.toEntity(stockDataDto))
                .thenThrow(new RuntimeException("Stock mapping failed"));

        // when + then
        assertThatThrownBy(() ->
                stockPersistenceService.saveStockWithMarketDays(
                        stockDataDto,
                        List.of(mock(MarketDayDto.class))
                )
        )
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Stock mapping failed");

        verifyNoInteractions(marketDayMapper);
        verifyNoInteractions(stockRepository);
    }

    @Test
    void saveStockWithMarketDays_shouldNotSaveStockWhenMarketDayMappingFails() {
        // given
        StockDataDto stockDataDto = mock(StockDataDto.class);
        MarketDayDto marketDayDto = mock(MarketDayDto.class);

        Stock stock = mock(Stock.class);

        when(stockMapper.toEntity(stockDataDto)).thenReturn(stock);
        when(marketDayMapper.toEntity(marketDayDto))
                .thenThrow(new RuntimeException("Market day mapping failed"));

        // when + then
        assertThatThrownBy(() ->
                stockPersistenceService.saveStockWithMarketDays(
                        stockDataDto,
                        List.of(marketDayDto)
                )
        )
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Market day mapping failed");

        verify(stockMapper).toEntity(stockDataDto);
        verify(marketDayMapper).toEntity(marketDayDto);
        verify(stock, never()).addMarketDay(any());
        verify(stockRepository, never()).save(any());
    }

    @Test
    void addOrUpdateMarketDay_shouldAddNewMarketDayWhenItDoesNotExist() {
        // given
        String ticker = "AAPL";

        Stock stock = mock(Stock.class);
        MarketDayDto marketDayDto = mock(MarketDayDto.class);
        MarketDay marketDay = mock(MarketDay.class);

        when(marketDayDto.date()).thenReturn(LocalDate.of(2026, 7, 3));

        when(stockRepository.findByTicker(ticker))
                .thenReturn(Optional.of(stock));

        when(marketDayRepository.findByStockTickerAndDate(ticker, LocalDate.of(2026, 7, 3)))
                .thenReturn(Optional.empty());

        when(marketDayMapper.toEntity(marketDayDto))
                .thenReturn(marketDay);

        // when
        stockPersistenceService.addOrUpdateMarketDay(ticker, marketDayDto);

        // then
        verify(stockRepository).findByTicker(ticker);
        verify(marketDayRepository).findByStockTickerAndDate(ticker, LocalDate.of(2026, 7, 3));
        verify(marketDayMapper).toEntity(marketDayDto);
        verify(stock).addMarketDay(marketDay);
    }

    @Test
    void addOrUpdateMarketDay_shouldUpdateExistingMarketDayWhenItAlreadyExists() {
        // given
        String ticker = "AAPL";
        LocalDate date = LocalDate.of(2026, 7, 3);

        Stock stock = mock(Stock.class);
        MarketDay existingMarketDay = mock(MarketDay.class);
        MarketDayDto marketDayDto = mock(MarketDayDto.class);

        when(marketDayDto.date()).thenReturn(date);

        when(stockRepository.findByTicker(ticker))
                .thenReturn(Optional.of(stock));

        when(marketDayRepository.findByStockTickerAndDate(ticker, date))
                .thenReturn(Optional.of(existingMarketDay));

        // when
        stockPersistenceService.addOrUpdateMarketDay(ticker, marketDayDto);

        // then
        verify(marketDayMapper).updateEntityFromDto(marketDayDto, existingMarketDay);
        verify(marketDayMapper, never()).toEntity(any());
        verify(stock, never()).addMarketDay(any());
    }

    @Test
    void updateStockData_shouldUpdateExistingStockAndSetLastUpdatedAt() {
        // given
        String ticker = "AAPL";

        Stock stock = mock(Stock.class);
        StockDataDto stockDataDto = mock(StockDataDto.class);

        when(stockRepository.findByTicker(ticker))
                .thenReturn(Optional.of(stock));

        // when
        stockPersistenceService.updateStockData(ticker, stockDataDto);

        // then
        verify(stockRepository).findByTicker(ticker);
        verify(stockMapper).updateEntityFromDto(stockDataDto, stock);
        verify(stock).setLastUpdatedAt(any(Instant.class));
    }

    @Test
    void getTickersForStockDataRefresh_shouldUseConfiguredLimit() {
        // given
        when(stockRepository.findTickersForStockDataRefresh(PageRequest.of(0, 100)))
                .thenReturn(List.of("AAPL", "MSFT"));

        // when
        List<String> result = stockPersistenceService.getTickersForStockDataRefresh(100);

        // then
        assertThat(result).containsExactly("AAPL", "MSFT");
        verify(stockRepository).findTickersForStockDataRefresh(PageRequest.of(0, 100));
    }
}