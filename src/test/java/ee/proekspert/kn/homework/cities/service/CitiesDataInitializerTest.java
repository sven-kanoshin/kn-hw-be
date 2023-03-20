package ee.proekspert.kn.homework.cities.service;

import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.event.ContextRefreshedEvent;

import java.util.List;

import ee.proekspert.kn.homework.cities.CitiesConfiguration;
import ee.proekspert.kn.homework.cities.model.City;
import ee.proekspert.kn.homework.cities.repository.CitiesRepository;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CitiesDataInitializerTest {

    @Mock
    private CsvMapper csvMapper;
    @Mock
    private CitiesRepository repository;
    @Mock
    private ContextRefreshedEvent event;
    @Mock
    private CitiesConfiguration config;

    
    private CitiesDataInitializer service;
    
    @BeforeEach
    void setUp() throws Exception {
        service = new CitiesDataInitializer(csvMapper, repository, config);
    }

    @Test
    void onApplicationEvent_existingData() {
        when(repository.count()).thenReturn(5L);
        
        service.onApplicationEvent(event);
        
        verify(repository, never()).saveAll(ArgumentMatchers.<List<City>>any());
    }
}
