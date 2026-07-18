package io.github.pavelnajmon.stockanalyzer.service;

import io.github.pavelnajmon.stockanalyzer.exception.DuplicateException;
import io.github.pavelnajmon.stockanalyzer.exception.EntityNotFoundException;
import io.github.pavelnajmon.stockanalyzer.model.dto.response.StockSummaryResponse;
import io.github.pavelnajmon.stockanalyzer.model.dto.response.WatchlistDetailResponse;
import io.github.pavelnajmon.stockanalyzer.model.entity.Stock;
import io.github.pavelnajmon.stockanalyzer.model.entity.User;
import io.github.pavelnajmon.stockanalyzer.model.entity.Watchlist;
import io.github.pavelnajmon.stockanalyzer.mapper.WatchlistMapper;
import io.github.pavelnajmon.stockanalyzer.model.dto.request.CreateWatchlistRequest;
import io.github.pavelnajmon.stockanalyzer.model.dto.response.WatchlistResponse;
import io.github.pavelnajmon.stockanalyzer.model.entity.WatchlistStock;
import io.github.pavelnajmon.stockanalyzer.model.enums.Attribute;
import io.github.pavelnajmon.stockanalyzer.model.enums.EntityType;
import io.github.pavelnajmon.stockanalyzer.model.enums.UserRole;
import io.github.pavelnajmon.stockanalyzer.repository.WatchlistRepository;
import io.github.pavelnajmon.stockanalyzer.repository.WatchlistStockRepository;
import io.github.pavelnajmon.stockanalyzer.security.CustomUserDetails;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
public class WatchlistServiceImpl implements WatchlistService {

    private final StockPersistenceService stockPersistenceService;
    private final WatchlistRepository watchlistRepository;
    private final WatchlistMapper watchlistMapper;
    private final WatchlistStockRepository watchlistStockRepository;

    public WatchlistServiceImpl(StockPersistenceService stockPersistenceService, WatchlistRepository watchlistRepository, WatchlistMapper watchlistMapper, WatchlistStockRepository watchlistStockRepository) {
        this.stockPersistenceService = stockPersistenceService;
        this.watchlistRepository = watchlistRepository;
        this.watchlistMapper = watchlistMapper;
        this.watchlistStockRepository = watchlistStockRepository;
    }

    @Override
    @Transactional
    @PreAuthorize("isAuthenticated()")
    public WatchlistResponse createWatchlist(CreateWatchlistRequest request, CustomUserDetails currentUser) {
        if (currentUser == null) {
            throw new AccessDeniedException("User is not authenticated");
        }

        User user = currentUser.getUser();
        String name = request.name();

        if (watchlistRepository.existsByUserAndName(user, name)) {
            throw new DuplicateException(Attribute.WATCHLIST_NAME);
        }

        Watchlist watchlist = new Watchlist();
        watchlist.setUser(user);
        watchlist.setName(name);
        watchlist.setCreatedAt(Instant.now());

        Watchlist savedWatchlist = watchlistRepository.save(watchlist);

        return watchlistMapper.toResponse(savedWatchlist);
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN') or @authorization.isOwner(#watchlistId, authentication)")
    public void deleteWatchlist(Long watchlistId, CustomUserDetails currentUser) {
        Watchlist watchlist = watchlistRepository.findById(watchlistId)
                .orElseThrow(() -> new EntityNotFoundException(EntityType.WATCHLIST));

        watchlistRepository.delete(watchlist);
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("isAuthenticated()")
    public List<WatchlistResponse> getWatchlists(CustomUserDetails userDetails) {
        if (userDetails.getRole() == UserRole.ADMIN) {
            return watchlistRepository.findAll()
                    .stream()
                    .map(watchlistMapper::toResponse)
                    .toList();
        }

        return watchlistRepository.findAllByUser(userDetails.getUser())
                .stream()
                .map(watchlistMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ADMIN') or @authorization.isOwner(#watchlistId, authentication)")
    public WatchlistDetailResponse getWatchlistDetail(Long watchlistId) {
        Watchlist watchlist = watchlistRepository.findById(watchlistId)
                .orElseThrow(() -> new EntityNotFoundException(EntityType.WATCHLIST));

        List<StockSummaryResponse> stockSummaryResponses = watchlist.getWatchlistStocks()
                .stream()
                .map(watchlistStock -> {
                    Long stockId = watchlistStock.getStock().getId();
                    return stockPersistenceService.getStockSummary(stockId);
                })
                .toList();

        return watchlistMapper.toDetailResponse(watchlist, stockSummaryResponses);
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN') or @authorization.isOwner(#watchlistId, authentication)")
    public void addStockToWatchlist(Long watchlistId, Long stockId) {
        Watchlist watchlist = watchlistRepository.findById(watchlistId).orElseThrow(() -> new EntityNotFoundException(EntityType.WATCHLIST));
        Stock stock = stockPersistenceService.getStockById(stockId);

        if (watchlistStockRepository.existsByWatchlistIdAndStockId(watchlistId, stockId)) {
            throw new DuplicateException("Stock with id " + stockId + " is already present in watchlist with id " + watchlistId);
        }

        WatchlistStock watchlistStock = new WatchlistStock();
        watchlistStock.setWatchlist(watchlist);
        watchlistStock.setStock(stock);
        watchlistStock.setAddedAt(Instant.now());

        watchlistStockRepository.save(watchlistStock);
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN') or @authorization.isOwner(#watchlistId, authentication)")
    public void removeStockFromWatchlist(Long watchlistId, Long stockId) {
        WatchlistStock watchlistStock = watchlistStockRepository.findByWatchlistIdAndStockId(watchlistId, stockId).orElseThrow(() -> new EntityNotFoundException(EntityType.STOCK));
        watchlistStockRepository.delete(watchlistStock);
    }
}