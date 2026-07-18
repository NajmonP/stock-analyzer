package io.github.pavelnajmon.stockanalyzer.mapper;

import io.github.pavelnajmon.stockanalyzer.model.dto.response.StockSummaryResponse;
import io.github.pavelnajmon.stockanalyzer.model.dto.response.WatchlistDetailResponse;
import io.github.pavelnajmon.stockanalyzer.model.entity.Watchlist;
import io.github.pavelnajmon.stockanalyzer.model.dto.response.WatchlistResponse;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class WatchlistMapper {

    public WatchlistResponse toResponse(Watchlist watchlist) {
        return new WatchlistResponse(
                watchlist.getId(),
                watchlist.getName()
        );
    }

    public WatchlistDetailResponse toDetailResponse(Watchlist watchlist, List<StockSummaryResponse> stockSummaryResponses) {
        return new WatchlistDetailResponse(
                watchlist.getId(),
                watchlist.getName(),
                watchlist.getCreatedAt(),
                stockSummaryResponses
        );
    }
}