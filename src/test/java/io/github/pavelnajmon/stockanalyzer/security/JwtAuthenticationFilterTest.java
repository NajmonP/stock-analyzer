package io.github.pavelnajmon.stockanalyzer.security;

import io.github.pavelnajmon.stockanalyzer.configuration.JwtProperties;
import io.github.pavelnajmon.stockanalyzer.model.entity.User;
import io.github.pavelnajmon.stockanalyzer.model.enums.UserRole;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class JwtAuthenticationFilterTest {

    private final JwtProperties jwtProperties = createJwtProperties();

    private final JwtService jwtService = new JwtService(jwtProperties);
    private final CustomUserDetailsService userDetailsService = mock(CustomUserDetailsService.class);

    private final JwtAuthenticationFilter filter =
            new JwtAuthenticationFilter(jwtService, userDetailsService);

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void doFilterInternal_shouldNotAuthenticate_whenAuthorizationHeaderIsMissing()
            throws ServletException, IOException {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();

        // when
        filter.doFilter(request, response, filterChain);

        // then
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verifyNoInteractions(userDetailsService);
    }

    @Test
    void doFilterInternal_shouldAuthenticateUser_whenBearerTokenIsValid()
            throws ServletException, IOException {
        // given
        CustomUserDetails userDetails = createUserDetails();

        String token = jwtService.generateToken(userDetails);

        when(userDetailsService.loadUserByUsername("tom")).thenReturn(userDetails);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + token);

        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();

        // when
        filter.doFilter(request, response, filterChain);

        // then
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        assertThat(authentication).isNotNull();
        assertThat(authentication.getPrincipal()).isEqualTo(userDetails);
        assertThat(authentication.getAuthorities())
                .extracting("authority")
                .containsExactly("ROLE_USER");
    }

    @Test
    void doFilterInternal_shouldNotAuthenticateUser_whenBearerTokenIsInvalid()
            throws ServletException, IOException {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer invalid-token");

        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();

        // when
        filter.doFilter(request, response, filterChain);

        // then
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    private CustomUserDetails createUserDetails() {
        User user = new User();
        user.setUsername("tom");
        user.setEmail("tom@example.com");
        user.setPasswordHash("hashedPassword");
        user.setUserRole(UserRole.USER);

        ReflectionTestUtils.setField(user, "id", 1L);

        return new CustomUserDetails(user);
    }

    private JwtProperties createJwtProperties() {
        JwtProperties properties = new JwtProperties();
        properties.setSecret("12345678901234567890123456789012345678901234567890");
        properties.setExpirationMs(3_600_000L);
        return properties;
    }
}