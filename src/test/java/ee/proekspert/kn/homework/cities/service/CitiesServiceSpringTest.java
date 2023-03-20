package ee.proekspert.kn.homework.cities.service;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.Optional;

import ee.proekspert.kn.homework.cities.model.City;
import ee.proekspert.kn.homework.model.NotFoundException;
import ee.proekspert.kn.homework.security.model.AccessPermissions;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class CitiesServiceSpringTest {
    
    // Disable initializer
    @MockBean
    private CitiesDataInitializer initializer;
    
    @Autowired
    private CitiesServices service;
    
    // Need the full context only for access testing
    @Nested
    class Access {
    
    	@Test
    	void update_noUser() {
            assertThrows(AuthenticationCredentialsNotFoundException.class, () -> service.update("ref", null));
    	}
    	
        @Test
        void list_noUser() {
            assertThrows(AuthenticationCredentialsNotFoundException.class, () -> service.getList(Optional.empty(), Optional.empty()));
        }
        
        @Test
        void search_noUser() {
            assertThrows(AuthenticationCredentialsNotFoundException.class, () -> service.search("query", Optional.empty(), Optional.empty()));
        }
    	
        @Test
        @WithMockUser(authorities = AccessPermissions.Cities.ALLOW_VIEW)
        void update_viewPermission() {
            assertThrows(AccessDeniedException.class, () -> service.update("ref", null));
        }	
        
        @Test
        @WithMockUser(authorities = AccessPermissions.Cities.ALLOW_VIEW)
        void list_viewPermission() {
            service.getList(Optional.empty(), Optional.empty());
        }
        
        
        @Test
        @WithMockUser(authorities = AccessPermissions.Cities.ALLOW_VIEW)
        void search_viewPermission() {
            service.search("query", Optional.empty(), Optional.empty());
        }
        
        @Test
        @WithMockUser(authorities = AccessPermissions.Cities.ALLOW_EDIT)
        void update_editPermission() {
            assertThrows(NotFoundException.class, () -> service.update("ref", City.builder().build()));
        }
        
        @Test
        @WithMockUser(authorities = AccessPermissions.Cities.ALLOW_EDIT)
        void list_editPermission() {
            assertThrows(AccessDeniedException.class, () -> service.getList(Optional.empty(), Optional.empty()));
        }
        
        
        @Test
        @WithMockUser(authorities = AccessPermissions.Cities.ALLOW_EDIT)
        void search_editPermission() {
            assertThrows(AccessDeniedException.class, () -> service.search("query", Optional.empty(), Optional.empty()));
        }

    }
}
