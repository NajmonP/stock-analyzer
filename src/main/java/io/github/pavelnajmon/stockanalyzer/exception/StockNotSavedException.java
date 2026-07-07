package io.github.pavelnajmon.stockanalyzer.exception;

import org.springframework.http.HttpStatusCode;

public class StockNotSavedException extends StockAnalyzerException {
    public StockNotSavedException(String ticker) {
        super(HttpStatusCode.valueOf(500), "Stock not found for ticker: " + ticker);
    }
}
