package ee.proekspert.kn.homework;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

import ee.proekspert.kn.homework.security.filter.HomeworkAuthenticationFilter;
import ee.proekspert.kn.homework.security.model.AccessRoles;
import ee.proekspert.kn.homework.security.repository.TokenAuthenticationRepository;

/**
 * General security configuration
 */
@Configuration
@EnableWebSecurity
@EnableAutoConfiguration(exclude = {SecurityAutoConfiguration.class})
public class WebSecurityConfiguration {

    @Bean
    protected PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    protected InMemoryUserDetailsManager userDetailsService(PasswordEncoder passwordEncoder) {
        UserDetails anon = User.withUsername("anon")
                .password(passwordEncoder.encode(""))
                .authorities(AccessRoles.ANON.getPermissions())
                .build();

        UserDetails user = User.withUsername("user")
                .password(passwordEncoder.encode("password"))
                .authorities(AccessRoles.USER.getPermissions())
                .build();

        UserDetails admin = User.withUsername("admin")
                .password(passwordEncoder.encode("admin"))
                .authorities(AccessRoles.ADMIN.getPermissions())
                .build();

        return new InMemoryUserDetailsManager(anon, user, admin);
    }

    @Bean
    protected AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
        
    @Bean
    protected SecurityFilterChain filterChain(HttpSecurity http, TokenAuthenticationRepository authRepo) throws Exception {
        http
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
            .formLogin().disable()
            .csrf().disable()
            .cors().and()
            .authorizeHttpRequests()
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()                
                .anyRequest().authenticated()
            .and()
            .addFilterAfter(new HomeworkAuthenticationFilter(authRepo), AnonymousAuthenticationFilter.class);
        
        return http.build();
    }
    
    @Bean
    protected CorsConfigurationSource corsConfigurationSource() {
        final CorsConfiguration configuration = new CorsConfiguration();

        configuration.addAllowedOriginPattern("*");
        configuration.setAllowedMethods(Arrays.asList("HEAD", "GET", "POST", "PUT"));
        configuration.setAllowCredentials(true);

        configuration.setAllowedHeaders(Arrays.asList(
                "Authorization",
                "Accept",
                "Cache-Control",
                "Content-Type",
                "Origin",
                "x-requested-with"
        ));

        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
