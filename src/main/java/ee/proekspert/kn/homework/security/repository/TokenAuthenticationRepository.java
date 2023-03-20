package ee.proekspert.kn.homework.security.repository;

import jakarta.servlet.http.HttpServletRequest;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

/**
 * DB backed repository for storing currently active tokens (a "session" in a sense)
 * 
 */
@Slf4j
@Service
public class TokenAuthenticationRepository {
    public static final Authentication ANON = new UsernamePasswordAuthenticationToken("ANON", "ANON");
    
    private final Map<String, Authentication> tokenRepository = new HashMap<>();
    
    public Authentication loadAuthentication(@NonNull HttpServletRequest request) {
        String token = this.extractToken(request);
        if (token != null && this.tokenRepository.containsKey(token)) {
            log.debug("::loadAuthentication; found token");
            return this.tokenRepository.get(token);
        }
        return ANON;
    }
    
    public boolean saveAuthentication(Authentication auth) {
        if ( auth == null ) {
            log.warn(":: saveAuthentication called with 'null' value");
            return false;
        }
        if ( auth.getCredentials() == null || String.valueOf(auth.getCredentials()).isEmpty()) {
            log.warn(":: saveAuthentication called with authentication having no credentials");
            return false;
        }
        this.tokenRepository.put(String.valueOf(auth.getCredentials()), auth);
        return true;
    }

    public boolean requestContainsToken(@NonNull HttpServletRequest request) {
        String token = this.extractToken(request);
        return (token != null && !token.isEmpty());
    }
    
    public void clearAuthentication(@NonNull HttpServletRequest request) {
        String token = this.extractToken(request);
        if (token != null) {
            this.tokenRepository.remove(token);
        }
    }
    
    // cleanup, once per hour
    @Scheduled(fixedRate=60*60*1000)
    public void cleanup() {
        log.debug("::cleanup");
        this.tokenRepository.clear();
    }
    
    private String extractToken(@NonNull HttpServletRequest request) {
        String auth = request.getHeader(AUTHORIZATION);
        if (auth != null && auth.startsWith("Bearer")) {
            String token = auth.substring(7, auth.length());
            return String.valueOf(token).trim();
        }
        return null;
    }
}
