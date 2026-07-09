package io.github.pavelnajmon.stockanalyzer.exception;

import io.github.pavelnajmon.stockanalyzer.model.enums.Attribute;
import org.springframework.http.HttpStatusCode;

public class DuplicateException extends StockAnalyzerException {
    public DuplicateException(Attribute attribute, Throwable cause) {
        super(HttpStatusCode.valueOf(409), buildMessage(attribute), cause);
    }

    public DuplicateException(Attribute attribute) {
        super(HttpStatusCode.valueOf(409), buildMessage(attribute));
    }

    private static String buildMessage(Attribute attribute) {
        String errorMessage = "";

        switch (attribute) {
            case USERNAME -> errorMessage = "Username already exists";
            case EMAIL -> errorMessage = "Email already exists";
            case TICKER -> errorMessage = "Stock with this ticker already exists";
            case WATCHLIST_NAME -> errorMessage = "You already have Watchlist with this name";
        }

        return errorMessage;
    }
}
