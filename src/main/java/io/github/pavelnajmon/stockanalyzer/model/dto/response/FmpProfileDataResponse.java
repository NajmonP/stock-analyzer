package io.github.pavelnajmon.stockanalyzer.model.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public record FmpProfileDataResponse(
        @JsonProperty("symbol")
        String ticker,
        String companyName,
        String description,
        String sector,
        String industry,
        String exchange,
        String currency,
        @JsonProperty("website")
        String websiteUrl,
        @JsonProperty("image")
        String logoUrl,
        @JsonProperty("marketCap")
        BigDecimal marketCapitalization,
        String range,
        BigDecimal beta
) {
}
