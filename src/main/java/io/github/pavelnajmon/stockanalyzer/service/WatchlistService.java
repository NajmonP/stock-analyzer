package io.github.pavelnajmon.stockanalyzer.service;

import io.github.pavelnajmon.stockanalyzer.model.dto.request.CreateWatchlistRequest;
import io.github.pavelnajmon.stockanalyzer.model.dto.response.WatchlistDetailResponse;
import io.github.pavelnajmon.stockanalyzer.model.dto.response.WatchlistResponse;
import io.github.pavelnajmon.stockanalyzer.security.CustomUserDetails;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface WatchlistService {

    WatchlistResponse createWatchlist(CreateWatchlistRequest request, CustomUserDetails currentUser);

    void deleteWatchlist(Long watchlistId, CustomUserDetails currentUser);

    List<WatchlistResponse> getWatchlists(CustomUserDetails userDetails);

    WatchlistDetailResponse getWatchlistDetail(Long watchlistId);

    void addStockToWatchlist(Long watchlistId, Long stockId);

    void removeStockFromWatchlist(Long watchlistId, Long stockId);
}