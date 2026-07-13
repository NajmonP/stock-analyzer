package io.github.pavelnajmon.stockanalyzer.repository;

import io.github.pavelnajmon.stockanalyzer.model.entity.WatchlistStock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WatchlistStockRepository extends JpaRepository<WatchlistStock, Long> {

    boolean existsByWatchlistIdAndStockId(Long watchlistId, Long stockId);

    Optional<WatchlistStock> findByWatchlistIdAndStockId(Long watchlistId, Long stockId);
}