package io.github.pavelnajmon.stockanalyzer.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateWatchlistRequest(

        @NotBlank(message = "Watchlist name must not be blank")
        @Size(max = 100, message = "Watchlist name must have at most 100 characters")
        String name

) {
}