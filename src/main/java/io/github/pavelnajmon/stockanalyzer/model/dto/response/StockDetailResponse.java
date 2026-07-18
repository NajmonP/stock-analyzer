package io.github.pavelnajmon.stockanalyzer.model.dto.response;

import io.github.pavelnajmon.stockanalyzer.model.dto.MarketDayDto;
import io.github.pavelnajmon.stockanalyzer.model.dto.StockDataDto;

import java.util.List;

public record StockDetailResponse(
        StockDataDto stockData,
        List<MarketDayDto> marketDays
) {
}
