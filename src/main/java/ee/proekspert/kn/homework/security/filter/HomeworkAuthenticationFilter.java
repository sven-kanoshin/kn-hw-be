package ee.proekspert.kn.homework.security.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;

import ee.proekspert.kn.homework.security.repository.TokenAuthenticationRepository;

/**
 * Verifies and authorizes Auth tokens, if they are present
 */
@Slf4j
@RequiredArgsConstructor
public class HomeworkAuthenticationFilter implements Filter {

    private final TokenAuthenticationRepository repository;
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        if (repository.requestContainsToken(req)) {
            Authentication auth = repository.loadAuthentication(req);
            log.debug("isAuthenticated? {}", auth.isAuthenticated());
            SecurityContext context = SecurityContextHolder.getContextHolderStrategy().createEmptyContext();
            context.setAuthentication(auth);
            SecurityContextHolder.getContextHolderStrategy().setContext(context);
        }
        chain.doFilter(request, response);
    }

}
