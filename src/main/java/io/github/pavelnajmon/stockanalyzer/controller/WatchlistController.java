package io.github.pavelnajmon.stockanalyzer.controller;

import io.github.pavelnajmon.stockanalyzer.model.dto.request.CreateWatchlistRequest;
import io.github.pavelnajmon.stockanalyzer.model.dto.response.WatchlistDetailResponse;
import io.github.pavelnajmon.stockanalyzer.model.dto.response.WatchlistResponse;
import io.github.pavelnajmon.stockanalyzer.security.CustomUserDetails;
import io.github.pavelnajmon.stockanalyzer.service.WatchlistService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/watchlists")
public class WatchlistController {

    private final WatchlistService watchlistService;

    public WatchlistController(WatchlistService watchlistService) {
        this.watchlistService = watchlistService;
    }

    @PostMapping("/add")
    public ResponseEntity<WatchlistResponse> createWatchlist(@Valid @RequestBody CreateWatchlistRequest request, @AuthenticationPrincipal CustomUserDetails currentUser) {
        WatchlistResponse response = watchlistService.createWatchlist(request, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/delete/{watchlistId}")
    public ResponseEntity<Void> deleteWatchlist(@PathVariable Long watchlistId, @AuthenticationPrincipal CustomUserDetails currentUser){
        watchlistService.deleteWatchlist(watchlistId, currentUser);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{watchlistId}/stocks/{stockId}")
    public ResponseEntity<Void> addStockToWatchlist(@PathVariable Long watchlistId, @PathVariable Long stockId) {
        watchlistService.addStockToWatchlist(watchlistId, stockId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{watchlistId}/stocks/{stockId}")
    public ResponseEntity<Void> removeStockFromWatchlist(@PathVariable Long watchlistId, @PathVariable Long stockId) {
        watchlistService.removeStockFromWatchlist(watchlistId, stockId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<WatchlistResponse>> getWatchlists(@AuthenticationPrincipal CustomUserDetails currentUser) {
        List<WatchlistResponse> response = watchlistService.getWatchlists(currentUser);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/detail/{watchlistId}")
    public ResponseEntity<WatchlistDetailResponse> getWatchlistDetail(@PathVariable Long watchlistId) {
        WatchlistDetailResponse response = watchlistService.getWatchlistDetail(watchlistId);
        return ResponseEntity.ok(response);
    }
}