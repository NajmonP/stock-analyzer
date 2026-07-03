package io.github.pavelnajmon.stockanalyzer.controller;

import io.github.pavelnajmon.stockanalyzer.service.StockService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}
