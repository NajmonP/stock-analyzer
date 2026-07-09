package io.github.pavelnajmon.stockanalyzer.service;

import io.github.pavelnajmon.stockanalyzer.exception.DuplicateException;
import io.github.pavelnajmon.stockanalyzer.model.dto.response.AuthenticationResponse;
import io.github.pavelnajmon.stockanalyzer.model.entity.User;
import io.github.pavelnajmon.stockanalyzer.model.enums.Attribute;
import io.github.pavelnajmon.stockanalyzer.model.enums.UserRole;
import io.github.pavelnajmon.stockanalyzer.model.dto.request.LoginRequest;
import io.github.pavelnajmon.stockanalyzer.model.dto.request.RegisterRequest;
import io.github.pavelnajmon.stockanalyzer.repository.UserRepository;
import io.github.pavelnajmon.stockanalyzer.security.CustomUserDetails;
import io.github.pavelnajmon.stockanalyzer.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthenticationService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtService jwtService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @Transactional
    public void register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new DuplicateException(Attribute.USERNAME);
        }

        if (userRepository.existsByEmail(request.email())) {
            throw new DuplicateException(Attribute.EMAIL);
        }

        User user = new User();
        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setUserRole(UserRole.USER);
        user.setCreatedAt(Instant.now());

        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public AuthenticationResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.usernameOrEmail(),
                        request.password()
                )
        );

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        String token = jwtService.generateToken(userDetails);

        return new AuthenticationResponse(
                token,
                "Bearer",
                jwtService.getExpirationMs() / 1000
        );
    }
}