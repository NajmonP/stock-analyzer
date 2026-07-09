package io.github.pavelnajmon.stockanalyzer.security;

import io.github.pavelnajmon.stockanalyzer.model.entity.User;
import io.github.pavelnajmon.stockanalyzer.model.enums.UserRole;
import io.github.pavelnajmon.stockanalyzer.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class CustomUserDetailsServiceTest {

    private final UserRepository appUserRepository = mock(UserRepository.class);

    private final CustomUserDetailsService service =
            new CustomUserDetailsService(appUserRepository);

    @Test
    void loadUserByUsername_shouldLoadUserByUsername() {
        // given
        User user = new User();
        user.setUsername("tom");
        user.setEmail("tom@example.com");
        user.setPasswordHash("hashedPassword");
        user.setUserRole(UserRole.USER);

        when(appUserRepository.findByUsername("tom")).thenReturn(Optional.of(user));

        // when
        UserDetails userDetails = service.loadUserByUsername("tom");

        // then
        assertThat(userDetails.getUsername()).isEqualTo("tom");
        assertThat(userDetails.getPassword()).isEqualTo("hashedPassword");
        assertThat(userDetails.getAuthorities())
                .extracting("authority")
                .containsExactly("ROLE_USER");

        verify(appUserRepository).findByUsername("tom");
        verify(appUserRepository, never()).findByEmail(anyString());
    }

    @Test
    void loadUserByUsername_shouldLoadUserByEmail_whenUsernameNotFound() {
        // given
        User user = new User();
        user.setUsername("tom");
        user.setEmail("tom@example.com");
        user.setPasswordHash("hashedPassword");
        user.setUserRole(UserRole.USER);

        when(appUserRepository.findByUsername("tom@example.com"))
                .thenReturn(Optional.empty());

        when(appUserRepository.findByEmail("tom@example.com"))
                .thenReturn(Optional.of(user));

        // when
        UserDetails userDetails = service.loadUserByUsername("tom@example.com");

        // then
        assertThat(userDetails.getUsername()).isEqualTo("tom");
        assertThat(userDetails.getAuthorities())
                .extracting("authority")
                .containsExactly("ROLE_USER");

        verify(appUserRepository).findByUsername("tom@example.com");
        verify(appUserRepository).findByEmail("tom@example.com");
    }

    @Test
    void loadUserByUsername_shouldThrowException_whenUserDoesNotExist() {
        // given
        when(appUserRepository.findByUsername("unknown")).thenReturn(Optional.empty());
        when(appUserRepository.findByEmail("unknown")).thenReturn(Optional.empty());

        // when + then
        assertThatThrownBy(() -> service.loadUserByUsername("unknown"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("User not found: unknown");
    }
}