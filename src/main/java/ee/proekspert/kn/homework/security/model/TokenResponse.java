package ee.proekspert.kn.homework.security.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TokenResponse {
    
    @JsonProperty("username")
    private final String username;
    
    @JsonProperty("token")
    private final String token;
}
