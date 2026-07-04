package io.github.pavelnajmon.stockanalyzer.provider;

import io.github.pavelnajmon.stockanalyzer.mapper.YahooHistoricalDataMapper;
import io.github.pavelnajmon.stockanalyzer.model.dto.MarketDayDto;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.JsonNode;

import java.util.List;

@Component
public class YahooFinanceHistoricalPriceProvider implements HistoricalPriceProvider {

    private static final String INTERVAL = "1d";
    private static final String RANGE = "10y";

    private final RestClient restClient;
    private final YahooHistoricalDataMapper yahooHistoricalDataMapper;

    public YahooFinanceHistoricalPriceProvider(RestClient.Builder restClientBuilder, YahooHistoricalDataMapper yahooHistoricalDataMapper) {
        this.restClient = restClientBuilder
                .baseUrl("https://query1.finance.yahoo.com")
                .defaultHeader("User-Agent", "Mozilla/5.0")
                .build();
        this.yahooHistoricalDataMapper = yahooHistoricalDataMapper;
    }

    @Override
    public List<MarketDayDto> getStockHistoricalPrices(String ticker) {
        JsonNode root = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v8/finance/chart/{ticker}")
                        .queryParam("range", RANGE)
                        .queryParam("interval", INTERVAL)
                        .queryParam("events", "history")
                        .build(ticker))
                .retrieve()
                .body(JsonNode.class);

        return yahooHistoricalDataMapper.toHistoricalPrices(root);
    }
}
