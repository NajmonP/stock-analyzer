package io.github.pavelnajmon.stockanalyzer.provider;

import io.github.pavelnajmon.stockanalyzer.mapper.YahooHistoricalDataMapper;
import io.github.pavelnajmon.stockanalyzer.model.dto.MarketDayDto;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.JsonNode;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Component
public class YahooFinanceHistoricalPriceProvider implements HistoricalPriceProvider {

    private static final String HISTORICAL_INTERVAL = "1d";
    private static final String HISTORICAL_RANGE = "10y";

    private static final String LATEST_INTERVAL = "1d";
    private static final String LATEST_RANGE = "5d";

    private final RestClient restClient;
    private final YahooHistoricalDataMapper yahooHistoricalDataMapper;

    public YahooFinanceHistoricalPriceProvider(
            RestClient.Builder restClientBuilder,
            YahooHistoricalDataMapper yahooHistoricalDataMapper
    ) {
        this.restClient = restClientBuilder
                .baseUrl("https://query1.finance.yahoo.com")
                .defaultHeader("User-Agent", "Mozilla/5.0")
                .build();
        this.yahooHistoricalDataMapper = yahooHistoricalDataMapper;
    }

    @Override
    public List<MarketDayDto> getStockHistoricalPrices(String ticker) {
        JsonNode root = getChartData(ticker, HISTORICAL_RANGE, HISTORICAL_INTERVAL);
        return yahooHistoricalDataMapper.toHistoricalPrices(root);
    }

    @Override
    public Optional<MarketDayDto> getLatestMarketDay(String ticker) {
        JsonNode root = getChartData(ticker, LATEST_RANGE, LATEST_INTERVAL);

        return yahooHistoricalDataMapper.toHistoricalPrices(root)
                .stream()
                .max(Comparator.comparing(MarketDayDto::date));
    }

    private JsonNode getChartData(String ticker, String range, String interval) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v8/finance/chart/{ticker}")
                        .queryParam("range", range)
                        .queryParam("interval", interval)
                        .queryParam("events", "history")
                        .build(ticker))
                .retrieve()
                .body(JsonNode.class);
    }
}