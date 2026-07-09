package io.github.pavelnajmon.stockanalyzer.controller;

import io.github.pavelnajmon.stockanalyzer.mapper.CurrentUserResponseMapper;
import io.github.pavelnajmon.stockanalyzer.model.dto.request.LoginRequest;
import io.github.pavelnajmon.stockanalyzer.model.dto.request.RegisterRequest;
import io.github.pavelnajmon.stockanalyzer.model.dto.response.AuthenticationResponse;
import io.github.pavelnajmon.stockanalyzer.model.dto.response.CurrentUserResponse;
import io.github.pavelnajmon.stockanalyzer.security.CustomUserDetails;
import io.github.pavelnajmon.stockanalyzer.service.AuthenticationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final CurrentUserResponseMapper currentUserResponseMapper;

    public AuthenticationController(AuthenticationService authenticationService, CurrentUserResponseMapper currentUserResponseMapper) {
        this.authenticationService = authenticationService;
        this.currentUserResponseMapper = currentUserResponseMapper;
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@Valid @RequestBody RegisterRequest request) {
        authenticationService.register(request);

        return ResponseEntity
                .created(URI.create("/api/auth/login"))
                .build();
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody LoginRequest request) {
        AuthenticationResponse response = authenticationService.login(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<CurrentUserResponse> currentUser(@AuthenticationPrincipal CustomUserDetails userDetails) {
        CurrentUserResponse response = currentUserResponseMapper.getCurrentUserResponse(userDetails);

        return ResponseEntity.ok(response);
    }
}