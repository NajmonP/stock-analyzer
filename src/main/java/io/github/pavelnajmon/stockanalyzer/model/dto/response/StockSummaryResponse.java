package io.github.pavelnajmon.stockanalyzer.model.dto.response;

import java.math.BigDecimal;

public record StockSummaryResponse(
        Long id,
        String ticker,
        String companyName,
        String sector,
        String industry,
        String logoUrl,
        BigDecimal lastClosePrice,
        BigDecimal marketCapitalization
) {
}
