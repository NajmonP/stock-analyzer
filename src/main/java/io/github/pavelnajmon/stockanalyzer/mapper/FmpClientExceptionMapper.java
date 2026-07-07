package io.github.pavelnajmon.stockanalyzer.mapper;

import io.github.pavelnajmon.stockanalyzer.exception.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

@Component
public class FmpClientExceptionMapper {
    public StockAnalyzerException mapFmpClientException(HttpClientErrorException ex, String ticker) {
        int statusCode = ex.getStatusCode().value();

        if (statusCode == 402) {
            return new ProviderAccessDeniedException(ticker, ex);
        }

        if (statusCode == 429) {
            return new ProviderRateLimitExceededException(ex);
        }

        if (statusCode == 404) {
            return new StockNotFoundException(ticker, ex);
        }

        return new DataNotProvidedException("Financial data provider rejected request for ticker: " + ticker);
    }
}
