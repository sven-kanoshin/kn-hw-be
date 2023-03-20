package ee.proekspert.kn.homework;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

/**
 * Security configuration for managing Method level security. Enables us to use @Secured annotation
 */
@Configuration
@EnableMethodSecurity(securedEnabled = true)
public class MethodSecurityConfiguration {

}
