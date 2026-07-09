package io.github.pavelnajmon.stockanalyzer.security;

import io.github.pavelnajmon.stockanalyzer.repository.WatchlistRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;


@Component("authorization")
public class AuthorizationService {

    private final WatchlistRepository watchlistRepository;

    public AuthorizationService(WatchlistRepository watchlistRepository) {
        this.watchlistRepository = watchlistRepository;
    }

    public boolean isOwner(Long watchlistId, Authentication authentication) {
        if (!(authentication.getPrincipal() instanceof CustomUserDetails currentUser)) {
            return false;
        }

        return watchlistRepository.existsByUserAndId(currentUser.getUser(), watchlistId);
    }
}
