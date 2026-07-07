package io.github.pavelnajmon.stockanalyzer.exception;

import org.springframework.http.HttpStatusCode;

public class DataNotProvidedException extends StockAnalyzerException {
    public DataNotProvidedException(String message) {
        super(HttpStatusCode.valueOf(502), message);
    }
}
