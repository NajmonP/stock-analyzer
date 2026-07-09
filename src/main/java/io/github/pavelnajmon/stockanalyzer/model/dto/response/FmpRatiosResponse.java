package io.github.pavelnajmon.stockanalyzer.model.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public record FmpRatiosResponse(
        @JsonProperty("priceToEarningsRatioTTM")
        BigDecimal peRatio,
        @JsonProperty("netIncomePerShareTTM")
        BigDecimal earningsPerShare,
        @JsonProperty("dividendYieldTTM")
        BigDecimal dividendYield,
        @JsonProperty("debtServiceCoverageRatioTTM")
        BigDecimal debtCoverageRatio,
        @JsonProperty("freeCashFlowPerShareTTM")
        BigDecimal freeCashFlowPerShare,
        @JsonProperty("operatingProfitMarginTTM")
        BigDecimal operatingMargin
) {
}
