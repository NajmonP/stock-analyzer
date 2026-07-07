package io.github.pavelnajmon.stockanalyzer.exception;

import org.springframework.http.HttpStatusCode;

public abstract class StockAnalyzerException extends RuntimeException {
    private final HttpStatusCode httpStatusCode;

    public StockAnalyzerException(HttpStatusCode httpStatusCode, String message) {
        super(message);
        this.httpStatusCode = httpStatusCode;
    }

    public  StockAnalyzerException(HttpStatusCode httpStatusCode, String message, Throwable cause) {
        super(message, cause);
        this.httpStatusCode = httpStatusCode;
    }

    public HttpStatusCode getHttpStatusCode() {
        return httpStatusCode;
    }
}
