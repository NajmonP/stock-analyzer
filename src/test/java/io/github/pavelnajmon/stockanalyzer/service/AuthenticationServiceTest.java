package io.github.pavelnajmon.stockanalyzer.service;

import io.github.pavelnajmon.stockanalyzer.exception.DuplicateException;
import io.github.pavelnajmon.stockanalyzer.model.entity.User;
import io.github.pavelnajmon.stockanalyzer.model.enums.UserRole;
import io.github.pavelnajmon.stockanalyzer.model.dto.request.LoginRequest;
import io.github.pavelnajmon.stockanalyzer.model.dto.request.RegisterRequest;
import io.github.pavelnajmon.stockanalyzer.model.dto.response.AuthenticationResponse;
import io.github.pavelnajmon.stockanalyzer.repository.UserRepository;
import io.github.pavelnajmon.stockanalyzer.security.CustomUserDetails;
import io.github.pavelnajmon.stockanalyzer.security.JwtService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class AuthenticationServiceTest {

    private final UserRepository userRepository = mock(UserRepository.class);
    private final PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
    private final AuthenticationManager authenticationManager = mock(AuthenticationManager.class);
    private final JwtService jwtService = mock(JwtService.class);

    private final AuthenticationService authenticationService = new AuthenticationService(
            userRepository,
            passwordEncoder,
            authenticationManager,
            jwtService
    );

    @Test
    void register_shouldSaveUserWithEncodedPasswordAndUserRole() {
        // given
        RegisterRequest request = new RegisterRequest(
                "tom",
                "tom@example.com",
                "plainPassword"
        );

        when(userRepository.existsByUsername("tom")).thenReturn(false);
        when(userRepository.existsByEmail("tom@example.com")).thenReturn(false);
        when(passwordEncoder.encode("plainPassword")).thenReturn("hashedPassword");

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        // when
        authenticationService.register(request);

        // then
        verify(userRepository).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();

        assertThat(savedUser.getUsername()).isEqualTo("tom");
        assertThat(savedUser.getEmail()).isEqualTo("tom@example.com");
        assertThat(savedUser.getPasswordHash()).isEqualTo("hashedPassword");
        assertThat(savedUser.getUserRole()).isEqualTo(UserRole.USER);

        verify(passwordEncoder).encode("plainPassword");
    }

    @Test
    void register_shouldThrowException_whenUsernameAlreadyExists() {
        // given
        RegisterRequest request = new RegisterRequest(
                "tom",
                "tom@example.com",
                "plainPassword"
        );

        when(userRepository.existsByUsername("tom")).thenReturn(true);

        // when + then
        assertThatThrownBy(() -> authenticationService.register(request))
                .isInstanceOf(DuplicateException.class)
                .hasMessage("Username already exists");

        verify(userRepository, never()).save(any());
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    void register_shouldThrowException_whenEmailAlreadyExists() {
        // given
        RegisterRequest request = new RegisterRequest(
                "tom",
                "tom@example.com",
                "plainPassword"
        );

        when(userRepository.existsByUsername("tom")).thenReturn(false);
        when(userRepository.existsByEmail("tom@example.com")).thenReturn(true);

        // when + then
        assertThatThrownBy(() -> authenticationService.register(request))
                .isInstanceOf(DuplicateException.class)
                .hasMessage("Email already exists");

        verify(userRepository, never()).save(any());
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    void login_shouldAuthenticateUserAndReturnJwtToken() {
        // given
        LoginRequest request = new LoginRequest(
                "tom",
                "plainPassword"
        );

        User user = new User();
        user.setUsername("tom");
        user.setEmail("tom@example.com");
        user.setPasswordHash("hashedPassword");
        user.setUserRole(UserRole.USER);
        

        CustomUserDetails userDetails = new CustomUserDetails(user);

        Authentication authentication = mock(Authentication.class);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);

        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(jwtService.generateToken(userDetails)).thenReturn("jwt-token");
        when(jwtService.getExpirationMs()).thenReturn(3_600_000L);

        ArgumentCaptor<UsernamePasswordAuthenticationToken> authTokenCaptor =
                ArgumentCaptor.forClass(UsernamePasswordAuthenticationToken.class);

        // when
        AuthenticationResponse response = authenticationService.login(request);

        // then
        verify(authenticationManager).authenticate(authTokenCaptor.capture());

        UsernamePasswordAuthenticationToken authToken = authTokenCaptor.getValue();

        assertThat(authToken.getPrincipal()).isEqualTo("tom");
        assertThat(authToken.getCredentials()).isEqualTo("plainPassword");

        assertThat(response.accessToken()).isEqualTo("jwt-token");
        assertThat(response.tokenType()).isEqualTo("Bearer");
        assertThat(response.expiresIn()).isEqualTo(3600);
    }
}