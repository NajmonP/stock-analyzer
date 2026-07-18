package io.github.pavelnajmon.stockanalyzer.controller;

import io.github.pavelnajmon.stockanalyzer.model.dto.response.StockDetailResponse;
import io.github.pavelnajmon.stockanalyzer.model.dto.response.StockSummaryResponse;
import io.github.pavelnajmon.stockanalyzer.service.StockService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stocks")
public class StockController {

    private final StockService stockService;

    public StockController(StockService stockService) {
        this.stockService = stockService;
    }

    @PostMapping("/add")
    public ResponseEntity<String> addStock(@RequestParam String ticker){
        stockService.addStock(ticker);
        return ResponseEntity.ok("stock added");
    }

    @GetMapping
    public ResponseEntity<List<StockSummaryResponse>> getStocks() {
        List<StockSummaryResponse> response = stockService.getStocks();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/detail/{stockId}")
    public ResponseEntity<StockDetailResponse> getStockDetail(@PathVariable Long stockId){
        StockDetailResponse response = stockService.getStockDetail(stockId);
        return ResponseEntity.ok(response);
    }
}
