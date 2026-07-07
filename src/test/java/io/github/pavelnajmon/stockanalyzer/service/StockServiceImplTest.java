package io.github.pavelnajmon.stockanalyzer.service;

import io.github.pavelnajmon.stockanalyzer.exception.TickerBlankException;
import io.github.pavelnajmon.stockanalyzer.model.dto.MarketDayDto;
import io.github.pavelnajmon.stockanalyzer.model.dto.StockDataDto;
import io.github.pavelnajmon.stockanalyzer.provider.HistoricalPriceProvider;
import io.github.pavelnajmon.stockanalyzer.provider.StockDataProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StockServiceImplTest {

    @Mock
    private StockDataProvider stockDataProvider;

    @Mock
    private HistoricalPriceProvider historicalPriceProvider;

    @Mock
    private StockPersistenceService stockPersistenceService;

    private StockServiceImpl stockService;

    @BeforeEach
    void setUp() {
        stockService = new StockServiceImpl(
                stockDataProvider,
                historicalPriceProvider,
                stockPersistenceService
        );
    }

    @Test
    void addStock_shouldNormalizeTickerBeforeCallingProviders() {
        // given
        StockDataDto stockDataDto = mock(StockDataDto.class);
        List<MarketDayDto> marketDays = List.of(mock(MarketDayDto.class));

        when(stockDataProvider.getStockData("AAPL")).thenReturn(stockDataDto);
        when(historicalPriceProvider.getStockHistoricalPrices("AAPL")).thenReturn(marketDays);

        // when
        stockService.addStock("  aapl  ");

        // then
        verify(stockDataProvider).getStockData("AAPL");
        verify(historicalPriceProvider).getStockHistoricalPrices("AAPL");
        verify(stockPersistenceService).saveStockWithMarketDays(stockDataDto, marketDays);
    }

    @Test
    void addStock_shouldCallProvidersAndPersistenceInCorrectOrder() {
        // given
        StockDataDto stockDataDto = mock(StockDataDto.class);
        List<MarketDayDto> marketDays = List.of(mock(MarketDayDto.class));

        when(stockDataProvider.getStockData("MSFT")).thenReturn(stockDataDto);
        when(historicalPriceProvider.getStockHistoricalPrices("MSFT")).thenReturn(marketDays);

        // when
        stockService.addStock("MSFT");

        // then
        InOrder inOrder = inOrder(
                stockDataProvider,
                historicalPriceProvider,
                stockPersistenceService
        );

        inOrder.verify(stockDataProvider).getStockData("MSFT");
        inOrder.verify(historicalPriceProvider).getStockHistoricalPrices("MSFT");
        inOrder.verify(stockPersistenceService).saveStockWithMarketDays(stockDataDto, marketDays);
    }

    @Test
    void addStock_shouldThrowExceptionWhenTickerIsNull() {
        // when + then
        assertThatThrownBy(() -> stockService.addStock(null))
                .isInstanceOf(TickerBlankException.class)
                .hasMessage("Ticker must not be blank.");

        verifyNoInteractions(stockDataProvider);
        verifyNoInteractions(historicalPriceProvider);
        verifyNoInteractions(stockPersistenceService);
    }

    @Test
    void addStock_shouldThrowExceptionWhenTickerIsBlank() {
        // when + then
        assertThatThrownBy(() -> stockService.addStock("   "))
                .isInstanceOf(TickerBlankException.class)
                .hasMessage("Ticker must not be blank.");

        verifyNoInteractions(stockDataProvider);
        verifyNoInteractions(historicalPriceProvider);
        verifyNoInteractions(stockPersistenceService);
    }

    @Test
    void addStock_shouldNotCallHistoricalProviderOrPersistenceWhenStockDataProviderFails() {
        // given
        when(stockDataProvider.getStockData("AAPL"))
                .thenThrow(new RuntimeException("FMP API error"));

        // when + then
        assertThatThrownBy(() -> stockService.addStock("AAPL"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("FMP API error");

        verify(stockDataProvider).getStockData("AAPL");
        verifyNoInteractions(historicalPriceProvider);
        verifyNoInteractions(stockPersistenceService);
    }

    @Test
    void addStock_shouldNotCallPersistenceWhenHistoricalPriceProviderFails() {
        // given
        StockDataDto stockDataDto = mock(StockDataDto.class);

        when(stockDataProvider.getStockData("AAPL")).thenReturn(stockDataDto);
        when(historicalPriceProvider.getStockHistoricalPrices("AAPL"))
                .thenThrow(new RuntimeException("Yahoo API error"));

        // when + then
        assertThatThrownBy(() -> stockService.addStock("AAPL"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Yahoo API error");

        verify(stockDataProvider).getStockData("AAPL");
        verify(historicalPriceProvider).getStockHistoricalPrices("AAPL");
        verifyNoInteractions(stockPersistenceService);
    }
}