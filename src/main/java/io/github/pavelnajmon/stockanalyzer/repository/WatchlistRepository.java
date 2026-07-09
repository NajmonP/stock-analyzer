package io.github.pavelnajmon.stockanalyzer.repository;

import io.github.pavelnajmon.stockanalyzer.model.entity.User;
import io.github.pavelnajmon.stockanalyzer.model.entity.Watchlist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WatchlistRepository extends JpaRepository<Watchlist, Long> {

    boolean existsByUserAndName(User user, String name);

    boolean existsByUserAndId(User user, Long id);

    List<Watchlist> findAllByUserOrderByCreatedAtDesc(User user);

    Optional<Watchlist> findByIdAndUser(Long id, User user);
}