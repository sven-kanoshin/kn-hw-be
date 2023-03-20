package ee.proekspert.kn.homework.security.service;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import ee.proekspert.kn.homework.security.repository.TokenAuthenticationRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT) 
public class TokenAuthenticationRepositoryTest {

    private final String BEARER = "Bearer";
    private final String EXISTING_TOKEN = "existing-token";
    private final String EXISTING_TOKEN2 = "existing-token2";
    private final String NOT_EXISTING_TOKEN = "non-token";
    
    @Mock
    HttpServletRequest request;
    
    TokenAuthenticationRepository repository;
    
    @BeforeEach
    void setup() {
        repository = new TokenAuthenticationRepository();
    }
    
    @Nested
    class SaveAuthentication {
        
        @Test        
        void nullAuth() {
            assertFalse(repository.saveAuthentication(null));
        }

        @Test
        void nullCredentials() {
            assertFalse(repository.saveAuthentication(new UsernamePasswordAuthenticationToken("", null)));
        }
        
        @Test
        void emptyCredentials() {
            assertFalse(repository.saveAuthentication(new UsernamePasswordAuthenticationToken("", "")));
        }
        
        @Test
        void happy() {
            repository.saveAuthentication(new UsernamePasswordAuthenticationToken("", EXISTING_TOKEN2));
            
            when(request.getHeader(AUTHORIZATION)).thenReturn(BEARER + ": " + EXISTING_TOKEN2);
            assertTrue(repository.requestContainsToken(request));
        }
    }
    
    @Nested
    class LoadAuthentication {
        
        static final String USERNAME = "correct_username";
        
        @BeforeEach
        void setup() {
            repository.saveAuthentication(new UsernamePasswordAuthenticationToken(USERNAME, EXISTING_TOKEN));
        }
        
        @Test
        void existingToken() {
            when(request.getHeader(AUTHORIZATION)).thenReturn(BEARER + ": " + EXISTING_TOKEN);
            
            Authentication auth = repository.loadAuthentication(request);
            
            assertEquals(EXISTING_TOKEN, auth.getCredentials());
            assertEquals(USERNAME, auth.getPrincipal());
        }
        
        @Test
        void notExistingToken() {
            when(request.getHeader(AUTHORIZATION)).thenReturn(BEARER + ": " + NOT_EXISTING_TOKEN);
                       
            assertEquals(TokenAuthenticationRepository.ANON, repository.loadAuthentication(request));
        }
        
        @Test
        void noToken() {
            when(request.getHeader(AUTHORIZATION)).thenReturn(null);
           
            assertEquals(TokenAuthenticationRepository.ANON, repository.loadAuthentication(request));            
        }
    }
    
    @Nested
    class ClearAuthentication {
        @BeforeEach
        void setup() {
            repository.saveAuthentication(new UsernamePasswordAuthenticationToken("", EXISTING_TOKEN));
            repository.saveAuthentication(new UsernamePasswordAuthenticationToken("", EXISTING_TOKEN2));
        }
        
        @Test
        void existingToken() {
            when(request.getHeader(AUTHORIZATION)).thenReturn(BEARER + ": " + EXISTING_TOKEN);
            
            repository.clearAuthentication(request);
            
            assertEquals(TokenAuthenticationRepository.ANON, repository.loadAuthentication(request));
            
            when(request.getHeader(AUTHORIZATION)).thenReturn(BEARER + ": " + EXISTING_TOKEN2);
            assertNotEquals(TokenAuthenticationRepository.ANON, repository.loadAuthentication(request));
        }
        
        @Test
        void notExistingToken() {
            when(request.getHeader(AUTHORIZATION)).thenReturn(BEARER + ": " + NOT_EXISTING_TOKEN);
            
            repository.clearAuthentication(request);
            
            when(request.getHeader(AUTHORIZATION)).thenReturn(BEARER + ": " + EXISTING_TOKEN);
            assertNotEquals(TokenAuthenticationRepository.ANON, repository.loadAuthentication(request));
            
            when(request.getHeader(AUTHORIZATION)).thenReturn(BEARER + ": " + EXISTING_TOKEN2);
            assertNotEquals(TokenAuthenticationRepository.ANON, repository.loadAuthentication(request));
        }
        
        @Test
        void noToken() {
            when(request.getHeader(AUTHORIZATION)).thenReturn(null);
            
            repository.clearAuthentication(request);
            
            when(request.getHeader(AUTHORIZATION)).thenReturn(BEARER + ": " + EXISTING_TOKEN);
            assertNotEquals(TokenAuthenticationRepository.ANON, repository.loadAuthentication(request));
            
            when(request.getHeader(AUTHORIZATION)).thenReturn(BEARER + ": " + EXISTING_TOKEN2);
            assertNotEquals(TokenAuthenticationRepository.ANON, repository.loadAuthentication(request));
        }
    }
    
    @Nested
    class RequestContainsToken {
        
        @BeforeEach
        void setup() {
            repository.saveAuthentication(new UsernamePasswordAuthenticationToken("", EXISTING_TOKEN));
        }
        
        @Test
        void exist() {
            when(request.getHeader(AUTHORIZATION)).thenReturn(BEARER + ": " + EXISTING_TOKEN);
            assertTrue(repository.requestContainsToken(request));
        }
        
        @Test
        void notExist_emptyToken() {
            when(request.getHeader(AUTHORIZATION)).thenReturn(BEARER + ": ");
            assertFalse(repository.requestContainsToken(request));
        }
        
        @Test
        void notExist_noToken() {
            when(request.getHeader(AUTHORIZATION)).thenReturn(null);
            
            assertFalse(repository.requestContainsToken(request));
        }        
    }    
}
