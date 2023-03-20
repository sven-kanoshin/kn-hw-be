package ee.proekspert.kn.homework.security;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

import ee.proekspert.kn.homework.security.model.LoginRequest;
import ee.proekspert.kn.homework.security.model.TokenResponse;
import ee.proekspert.kn.homework.security.repository.TokenAuthenticationRepository;

/**
 * JSON-body based login. Need to send '{"username":"{user}", "credentials":"{credentials}"}'
 */
@Slf4j
@RestController
@RequestMapping(path = LoginController.PATH, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE )
@RequiredArgsConstructor
public class LoginController {

    public static final String PATH = "/api/auth";
    
    private final AuthenticationManager authManager;
    private final TokenAuthenticationRepository repository;
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        log.debug("::login attempt");
        Authentication auth = authManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getCredentials()));
        UUID token = UUID.randomUUID();
        
        User usr = (User)auth.getPrincipal();
        UsernamePasswordAuthenticationToken tokenAuth = UsernamePasswordAuthenticationToken.authenticated(usr.getUsername(), token, auth.getAuthorities());
        tokenAuth.setDetails(auth.getDetails());

        repository.saveAuthentication(tokenAuth);        
        return ResponseEntity.ofNullable(new TokenResponse(String.valueOf(tokenAuth.getPrincipal()), String.valueOf(tokenAuth.getCredentials())));
    }
    
    @GetMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        log.debug("::logout");
        repository.clearAuthentication(request);
        return ResponseEntity.noContent().build();
    }
}
