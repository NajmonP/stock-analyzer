package io.github.pavelnajmon.stockanalyzer.exception;

import org.springframework.http.HttpStatusCode;

public class ProviderRateLimitExceededException extends StockAnalyzerException {
    public ProviderRateLimitExceededException() {
        super(HttpStatusCode.valueOf(503), "Financial data provider rate limit was exceeded");
    }

    public ProviderRateLimitExceededException(Throwable cause) {
        super(HttpStatusCode.valueOf(503), "Financial data provider rate limit was exceeded",  cause);
    }
}
