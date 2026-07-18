package io.github.pavelnajmon.stockanalyzer.repository;

import io.github.pavelnajmon.stockanalyzer.model.entity.MarketDay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface MarketDayRepository extends JpaRepository<MarketDay, Long> {
    Optional<MarketDay> findByStockTickerAndDate(String ticker, LocalDate date);

    @Query("""
            select md.closePrice
            from MarketDay md
            where md.stock.ticker = :ticker
            order by md.date desc
            limit 1
            """)
    Optional<BigDecimal> findLastClosePriceByStockTicker(String ticker);
}
