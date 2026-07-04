package io.github.pavelnajmon.stockanalyzer.provider;

import io.github.pavelnajmon.stockanalyzer.mapper.YahooHistoricalDataMapper;
import io.github.pavelnajmon.stockanalyzer.model.dto.MarketDayDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.JsonNode;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.client.ExpectedCount.once;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class YahooFinanceHistoricalPriceProviderTest {

    private static final String BASE_URL = "https://query1.finance.yahoo.com";

    private MockRestServiceServer server;
    private YahooHistoricalDataMapper yahooHistoricalDataMapper;
    private YahooFinanceHistoricalPriceProvider provider;

    @BeforeEach
    void setUp() {
        RestClient.Builder restClientBuilder = RestClient.builder();
        server = MockRestServiceServer.bindTo(restClientBuilder).build();

        yahooHistoricalDataMapper = mock(YahooHistoricalDataMapper.class);

        provider = new YahooFinanceHistoricalPriceProvider(
                restClientBuilder,
                yahooHistoricalDataMapper
        );
    }

    @Test
    void getStockHistoricalPrices_shouldCallYahooEndpointAndReturnMappedMarketDays() {
        // given
        String yahooJson = """
                {
                  "chart": {
                    "result": [
                      {
                        "timestamp": [1717200000],
                        "indicators": {
                          "quote": [
                            {
                              "open": [190.0],
                              "high": [195.0],
                              "low": [188.0],
                              "close": [193.0],
                              "volume": [1000000]
                            }
                          ],
                          "adjclose": [
                            {
                              "adjclose": [193.0]
                            }
                          ]
                        }
                      }
                    ],
                    "error": null
                  }
                }
                """;

        MarketDayDto marketDayDto = mock(MarketDayDto.class);
        List<MarketDayDto> expectedMarketDays = List.of(marketDayDto);

        server.expect(
                        once(),
                        requestTo(BASE_URL + "/v8/finance/chart/AAPL?range=10y&interval=1d&events=history")
                )
                .andExpect(header("User-Agent", "Mozilla/5.0"))
                .andRespond(withSuccess(yahooJson, MediaType.APPLICATION_JSON));

        when(yahooHistoricalDataMapper.toHistoricalPrices(any(JsonNode.class)))
                .thenReturn(expectedMarketDays);

        // when
        List<MarketDayDto> result = provider.getStockHistoricalPrices("AAPL");

        // then
        assertThat(result).isSameAs(expectedMarketDays);

        verify(yahooHistoricalDataMapper).toHistoricalPrices(any(JsonNode.class));

        server.verify();
    }
}