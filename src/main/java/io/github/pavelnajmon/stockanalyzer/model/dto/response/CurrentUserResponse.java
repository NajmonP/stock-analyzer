package io.github.pavelnajmon.stockanalyzer.model.dto.response;

public record CurrentUserResponse(
        Long userId,
        String username,
        String email,
        String role
) {
}
