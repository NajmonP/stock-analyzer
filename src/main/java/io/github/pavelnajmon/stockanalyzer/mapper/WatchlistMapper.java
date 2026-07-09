package io.github.pavelnajmon.stockanalyzer.mapper;

import io.github.pavelnajmon.stockanalyzer.model.entity.Watchlist;
import io.github.pavelnajmon.stockanalyzer.model.dto.response.WatchlistResponse;
import org.springframework.stereotype.Component;

@Component
public class WatchlistMapper {

    public WatchlistResponse toResponse(Watchlist watchlist) {
        return new WatchlistResponse(
                watchlist.getId(),
                watchlist.getName()
        );
    }
}