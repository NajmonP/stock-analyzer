package io.github.pavelnajmon.stockanalyzer.exception;

import io.github.pavelnajmon.stockanalyzer.model.enums.EntityType;
import org.springframework.http.HttpStatusCode;

public class EntityNotFoundException extends StockAnalyzerException {
    public EntityNotFoundException(EntityType entityType) {
        super(HttpStatusCode.valueOf(404), buildMessage(entityType));
    }

    private static String buildMessage(EntityType entityType) {
        String errorMessage = "";

        switch (entityType) {
            case USER -> errorMessage = "User not found in database";
            case WATCHLIST -> errorMessage = "Watchlist not found in database";
            case MARKET_DAY -> errorMessage = "Market day not found in database";
            case STOCK -> errorMessage = "Stock not found in database";
        }

        return errorMessage;
    }
}
