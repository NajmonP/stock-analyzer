package io.github.pavelnajmon.stockanalyzer.model.dto;

import java.math.BigDecimal;

public record StockDataDto(
        String ticker,
        String companyName,
        String description,
        String sector,
        String industry,
        String exchange,
        String currency,
        String websiteUrl,
        String logoUrl,
        BigDecimal marketCapitalization,
        BigDecimal fiftyTwoWeekLow,
        BigDecimal fiftyTwoWeekHigh,
        BigDecimal peRatio,
        BigDecimal earningsPerShare,
        BigDecimal dividendYield,
        BigDecimal beta,
        BigDecimal debtServiceCoverageRatio,
        BigDecimal freeCashFlowPerShare,
        BigDecimal operatingMargin
) {
}
