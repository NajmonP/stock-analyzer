package io.github.pavelnajmon.stockanalyzer.service;

import io.github.pavelnajmon.stockanalyzer.model.dto.response.StockDetailResponse;
import io.github.pavelnajmon.stockanalyzer.model.dto.response.StockSummaryResponse;
import io.github.pavelnajmon.stockanalyzer.model.entity.Stock;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface StockService {
    void addStock(String sticker);

    List<StockSummaryResponse> getStocks();

    StockDetailResponse getStockDetail(Long stockId);
}
