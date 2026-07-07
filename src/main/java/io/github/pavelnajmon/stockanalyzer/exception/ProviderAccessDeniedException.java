package io.github.pavelnajmon.stockanalyzer.exception;

import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.HttpClientErrorException;

public class ProviderAccessDeniedException extends StockAnalyzerException {
    public ProviderAccessDeniedException(String ticker) {
        super( HttpStatusCode.valueOf(502), "Financial data for ticker " + ticker + " is not available with the current data provider plan");
    }

    public ProviderAccessDeniedException(String ticker, Throwable cause) {
        super( HttpStatusCode.valueOf(502), "Financial data for ticker " + ticker + " is not available with the current data provider plan", cause);
    }
}
