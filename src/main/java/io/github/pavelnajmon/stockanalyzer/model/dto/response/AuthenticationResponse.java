package io.github.pavelnajmon.stockanalyzer.model.dto.response;

public record AuthenticationResponse (
        String accessToken,
        String tokenType,
        long expiresIn
){

}
