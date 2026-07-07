package io.github.pavelnajmon.stockanalyzer.exception;

import org.springframework.http.HttpStatusCode;

public class TickerBlankException extends StockAnalyzerException {
    public TickerBlankException() {
        super(HttpStatusCode.valueOf(400), "Ticker must not be blank.");
    }
}
