package io.github.pavelnajmon.stockanalyzer.repository;

import io.github.pavelnajmon.stockanalyzer.model.entity.Stock;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StockRepository extends JpaRepository<Stock,Long> {
    Optional<Stock> findByTicker(String ticker);

    boolean existsByTicker(String ticker);

    @Query("select s.ticker from Stock s order by s.ticker asc")
    List<String> findAllTickers();

    @Query("""
            select s.ticker
            from Stock s
            order by
                case when s.lastUpdatedAt is null then 0 else 1 end,
                s.lastUpdatedAt asc,
                s.ticker asc
            """)
    List<String> findTickersForStockDataRefresh(Pageable pageable);
}
