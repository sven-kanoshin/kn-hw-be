package ee.proekspert.kn.homework.cities;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import ee.proekspert.kn.homework.model.EnvelopedResponse;
import ee.proekspert.kn.homework.security.injector.CityPermissionInjector;
import ee.proekspert.kn.homework.security.injector.PermissionInjectionAdvice;
import ee.proekspert.kn.homework.security.model.AccessPermissions;
import ee.proekspert.kn.homework.security.model.AllowedAction;

import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Import({CitiesTestConfiguration.class})
public class CitiesControllerSpringTest {

    @Autowired
    WebApplicationContext wac;
    
    @Autowired
    CitiesController controller;
    
    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new PermissionInjectionAdvice(List.of(new CityPermissionInjector())))
                .build();
    }
    
    @Test 
    @WithMockUser(authorities = AccessPermissions.Cities.ALLOW_VIEW)
    public void getCitites_viewPermissions() throws Exception {
      mockMvc.perform(get(CitiesController.PATH))
        .andExpect(jsonPath("$.status").value(EnvelopedResponse.MSG_SUCCESS))
        .andExpect(jsonPath("$.pageSize").value(3))        
        .andExpect(jsonPath("$.pageNumber").value(0))        
        .andExpect(jsonPath("$.itemCount").value(3))        
        .andExpect(jsonPath("$.totalPages").value(1))        
        .andExpect(jsonPath("$.totalSize").value(3))        
        .andExpect(jsonPath("$.payload[0].permissions").isArray())
        .andExpect(jsonPath("$.payload[0].permissions").isEmpty())
        .andExpect(jsonPath("$.payload[1].permissions").isArray())
        .andExpect(jsonPath("$.payload[1].permissions").isEmpty())
        .andExpect(jsonPath("$.payload[2].permissions").isArray())
        .andExpect(jsonPath("$.payload[2].permissions").isEmpty())        
        .andExpect(jsonPath("$.payload[3]").doesNotExist())
        .andExpect(status().isOk());
    }
    
    @Test 
    @WithMockUser(authorities = {AccessPermissions.Cities.ALLOW_VIEW, AccessPermissions.Cities.ALLOW_EDIT})
    public void getCitites_editPermissions() throws Exception {
      mockMvc.perform(get(CitiesController.PATH))
        .andExpect(jsonPath("$.status").value(EnvelopedResponse.MSG_SUCCESS))
        .andExpect(jsonPath("$.pageSize").value(3))        
        .andExpect(jsonPath("$.pageNumber").value(0))        
        .andExpect(jsonPath("$.itemCount").value(3))        
        .andExpect(jsonPath("$.totalPages").value(1))        
        .andExpect(jsonPath("$.totalSize").value(3))        
        .andExpect(jsonPath("$.payload[0].permissions").isArray())
        .andExpect(jsonPath("$.payload[0].permissions", hasItem(AllowedAction.EDIT.name())))
        .andExpect(jsonPath("$.payload[1].permissions").isArray())
        .andExpect(jsonPath("$.payload[1].permissions", hasItem(AllowedAction.EDIT.name())))
        .andExpect(jsonPath("$.payload[2].permissions").isArray())
        .andExpect(jsonPath("$.payload[2].permissions", hasItem(AllowedAction.EDIT.name())))       
        .andExpect(jsonPath("$.payload[3]").doesNotExist())
        .andExpect(status().isOk());
    }    
}

