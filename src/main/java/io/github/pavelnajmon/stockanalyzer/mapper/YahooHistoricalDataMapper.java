package io.github.pavelnajmon.stockanalyzer.mapper;

import io.github.pavelnajmon.stockanalyzer.model.dto.MarketDayDto;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

@Component
public class YahooHistoricalDataMapper {
    private YahooHistoricalDataMapper() {}

    public List<MarketDayDto> toHistoricalPrices(JsonNode root) {
        JsonNode result = root
                .path("chart")
                .path("result")
                .path(0);

        if (result.isMissingNode() || result.isNull()) {
            throw new IllegalArgumentException("Historical prices not found");
        }

        JsonNode timestamps = result.path("timestamp");

        JsonNode quote = result
                .path("indicators")
                .path("quote")
                .path(0);

        if (!timestamps.isArray() || !quote.isObject()) {
            return List.of();
        }

        JsonNode open = quote.path("open");
        JsonNode high = quote.path("high");
        JsonNode low = quote.path("low");
        JsonNode close = quote.path("close");
        JsonNode volume = quote.path("volume");

        List<MarketDayDto> prices = new ArrayList<>();

        for (int i = 0; i < timestamps.size(); i++) {
            BigDecimal openPrice = getBigDecimalValue(open, i);
            BigDecimal highPrice = getBigDecimalValue(high, i);
            BigDecimal lowPrice = getBigDecimalValue(low, i);
            BigDecimal closePrice = getBigDecimalValue(close, i);
            Long volumeValue = getLongValue(volume, i);

            if (openPrice == null || highPrice == null || lowPrice == null || closePrice == null) {
                continue;
            }

            LocalDate date = Instant.ofEpochSecond(timestamps.get(i).asLong())
                    .atZone(ZoneOffset.UTC)
                    .toLocalDate();

            prices.add(new MarketDayDto(
                    date,
                    openPrice,
                    highPrice,
                    lowPrice,
                    closePrice,
                    volumeValue
            ));
        }

        return prices;
    }

    private BigDecimal getBigDecimalValue(JsonNode values, int index) {
        if (!values.isArray() || index >= values.size() || values.get(index).isNull()) {
            return null;
        }

        return BigDecimal.valueOf(values.get(index).asDouble());
    }

    private Long getLongValue(JsonNode values, int index) {
        if (!values.isArray() || index >= values.size() || values.get(index).isNull()) {
            return null;
        }

        return values.get(index).asLong();
    }
}
