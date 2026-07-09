package io.github.pavelnajmon.stockanalyzer.security;

import io.github.pavelnajmon.stockanalyzer.configuration.JwtProperties;
import io.github.pavelnajmon.stockanalyzer.model.entity.User;
import io.github.pavelnajmon.stockanalyzer.model.enums.UserRole;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtServiceTest {

    private final JwtProperties jwtProperties = createJwtProperties();

    private final JwtService jwtService = new JwtService(jwtProperties);

    @Test
    void generateToken_shouldCreateValidTokenForUser() {
        // given
        CustomUserDetails userDetails = createUserDetails(
                1L,
                "tom",
                "tom@example.com",
                UserRole.USER
        );

        // when
        String token = jwtService.generateToken(userDetails);

        // then
        assertThat(token).isNotBlank();
        assertThat(jwtService.extractUsername(token)).isEqualTo("tom");
        assertThat(jwtService.isTokenValid(token, userDetails)).isTrue();
    }

    @Test
    void isTokenValid_shouldReturnFalse_whenTokenBelongsToDifferentUser() {
        // given
        CustomUserDetails tom = createUserDetails(
                1L,
                "tom",
                "tom@example.com",
                UserRole.USER
        );

        CustomUserDetails anna = createUserDetails(
                2L,
                "anna",
                "anna@example.com",
                UserRole.USER
        );

        String token = jwtService.generateToken(tom);

        // when
        boolean valid = jwtService.isTokenValid(token, anna);

        // then
        assertThat(valid).isFalse();
    }

    @Test
    void extractUsername_shouldThrowException_whenTokenIsTampered() {
        // given
        CustomUserDetails userDetails = createUserDetails(
                1L,
                "tom",
                "tom@example.com",
                UserRole.USER
        );

        String token = jwtService.generateToken(userDetails);
        String tamperedToken = token.substring(0, token.length() - 3) + "abc";

        // when + then
        assertThatThrownBy(() -> jwtService.extractUsername(tamperedToken))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void getExpirationMs_shouldReturnConfiguredExpiration() {
        assertThat(jwtService.getExpirationMs()).isEqualTo(3_600_000L);
    }

    private CustomUserDetails createUserDetails(
            Long userId,
            String username,
            String email,
            UserRole role
    ) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPasswordHash("hashedPassword");
        user.setUserRole(role);

        ReflectionTestUtils.setField(user, "id", userId);

        return new CustomUserDetails(user);
    }

    private JwtProperties createJwtProperties() {
        JwtProperties properties = new JwtProperties();
        properties.setSecret("12345678901234567890123456789012345678901234567890");
        properties.setExpirationMs(3_600_000L);
        return properties;
    }
}