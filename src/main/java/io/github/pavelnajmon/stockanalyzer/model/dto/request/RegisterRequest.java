package io.github.pavelnajmon.stockanalyzer.model.dto.request;

public record RegisterRequest(
        String username,
        String email,
        String password
) {
}