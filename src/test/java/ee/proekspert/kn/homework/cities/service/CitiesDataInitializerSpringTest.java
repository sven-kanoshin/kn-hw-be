package ee.proekspert.kn.homework.cities.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import ee.proekspert.kn.homework.cities.CitiesTestConfiguration;
import ee.proekspert.kn.homework.cities.model.City;
import ee.proekspert.kn.homework.cities.repository.CitiesRepository;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
@Import(CitiesTestConfiguration.class)
class CitiesDataInitializerSpringTest {    
    @Autowired
    private CitiesRepository repository;

    @Test
    void onApplicationEvent() {        
        City tallinn = repository.findById(591L).get();

        assertAll(
            () -> assertEquals(3, repository.count()),
            () -> assertEquals(591, tallinn.getId()),
            () -> assertEquals("Tallinn", tallinn.getName()),
            () -> assertEquals("https://upload.wikimedia.org/wikipedia/commons/thumb/5/5f/Tallinncollage2.jpg/418px-Tallinncollage2.jpg", tallinn.getImgUrl()),
            () -> assertNotNull(tallinn.getReference()),
            
            () -> assertNotNull(repository.findById(1L).orElse(null)),
            () -> assertNotNull(repository.findById(2L).orElse(null)),
            () -> assertNotNull(repository.findById(591L).orElse(null)),
            
            () -> assertNull(repository.findById(4L).orElse(null))        
        );
    }
}
