package io.github.pavelnajmon.stockanalyzer.exception;

import org.springframework.http.HttpStatusCode;

public class StockNotFoundException extends StockAnalyzerException {
    public StockNotFoundException(String ticker) {
        super(HttpStatusCode.valueOf(404), "Stock profile not found for ticker: " + ticker);
    }

    public StockNotFoundException(String ticker, Throwable cause) {
        super(HttpStatusCode.valueOf(404), "Stock profile not found for ticker: " + ticker, cause);
    }
}
