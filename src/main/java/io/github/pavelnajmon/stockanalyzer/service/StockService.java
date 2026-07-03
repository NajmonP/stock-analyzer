package io.github.pavelnajmon.stockanalyzer.service;

import io.github.pavelnajmon.stockanalyzer.model.entity.Stock;
import org.springframework.stereotype.Service;

@Service
public interface StockService {
    public void addStock(String sticker);
}
