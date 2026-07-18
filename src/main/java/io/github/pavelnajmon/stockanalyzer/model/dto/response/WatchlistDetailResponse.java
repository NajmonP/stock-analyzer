package io.github.pavelnajmon.stockanalyzer.model.dto.response;

import java.time.Instant;
import java.util.List;

public record WatchlistDetailResponse(
        Long id,
        String name,
        Instant createdAt,
        List<StockSummaryResponse> stocks
) {
}
