package ee.proekspert.kn.homework.cities;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ByteArrayResource;

import ee.proekspert.kn.homework.HomeworkApplication;

import static org.mockito.Mockito.when;

@Configuration
@Import(HomeworkApplication.class)
public class CitiesTestConfiguration {
    
    static final String INPUT = """
            id,name,photo
            1,Tokyo,https://upload.wikimedia.org/wikipedia/commons/thumb/b/b2/Skyscrapers_of_Shinjuku_2009_January.jpg/500px-Skyscrapers_of_Shinjuku_2009_January.jpg
            2,Jakarta,https://upload.wikimedia.org/wikipedia/commons/thumb/f/f6/Jakarta_Pictures-1.jpg/327px-Jakarta_Pictures-1.jpg
            591,Tallinn,https://upload.wikimedia.org/wikipedia/commons/thumb/5/5f/Tallinncollage2.jpg/418px-Tallinncollage2.jpg                
            """;
    
    @Bean
    @Primary
    public CitiesConfiguration configuration() {
        CitiesConfiguration config = Mockito.mock(CitiesConfiguration.class);
        
        when(config.getInitialDataFile()).thenReturn(new ByteArrayResource(INPUT.getBytes()));
        
        return config;
    }

}