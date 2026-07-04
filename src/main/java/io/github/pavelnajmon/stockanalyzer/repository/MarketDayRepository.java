package io.github.pavelnajmon.stockanalyzer.repository;

import io.github.pavelnajmon.stockanalyzer.model.entity.MarketDay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface MarketDayRepository extends JpaRepository<MarketDay, Long> {
    Optional<MarketDay> findByStockTickerAndDate(String ticker, LocalDate date);
}
