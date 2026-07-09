package io.github.pavelnajmon.stockanalyzer.service;

import io.github.pavelnajmon.stockanalyzer.model.dto.request.CreateWatchlistRequest;
import io.github.pavelnajmon.stockanalyzer.model.dto.response.WatchlistResponse;
import io.github.pavelnajmon.stockanalyzer.security.CustomUserDetails;

import java.util.List;

public interface WatchlistService {

    WatchlistResponse createWatchlist(CreateWatchlistRequest request, CustomUserDetails currentUser);

    void deleteWatchlist(Long watchlistId, CustomUserDetails currentUser);

    List<WatchlistResponse> getCurrentUserWatchlists(CustomUserDetails currentUser);
}