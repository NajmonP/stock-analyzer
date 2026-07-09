package io.github.pavelnajmon.stockanalyzer.model.dto.request;

import com.fasterxml.jackson.annotation.JsonAlias;

public record LoginRequest(
        @JsonAlias({"username", "email"})
        String usernameOrEmail,
        String password
) {
}