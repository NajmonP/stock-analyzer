package io.github.pavelnajmon.stockanalyzer.model.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record MarketDayDto(
        LocalDate date,
        BigDecimal openPrice,
        BigDecimal highPrice,
        BigDecimal lowPrice,
        BigDecimal closePrice,
        Long volume
) {
}
