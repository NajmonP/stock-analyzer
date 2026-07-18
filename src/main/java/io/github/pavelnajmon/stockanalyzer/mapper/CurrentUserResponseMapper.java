package io.github.pavelnajmon.stockanalyzer.mapper;

import io.github.pavelnajmon.stockanalyzer.model.dto.response.CurrentUserResponse;
import io.github.pavelnajmon.stockanalyzer.security.CustomUserDetails;
import org.springframework.stereotype.Component;

@Component
public class CurrentUserResponseMapper {
    public CurrentUserResponse getCurrentUserResponse(CustomUserDetails userDetails){
        return new CurrentUserResponse(
                userDetails.getUserId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                userDetails.getRoleName()
        );
    }
}
