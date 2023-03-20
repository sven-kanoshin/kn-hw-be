package ee.proekspert.kn.homework.cities;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

@Configuration
public class CitiesConfiguration {

    private static final String CITIES_DATA_FILENAME = "cities.csv";
    
    @Getter
    @Value("classpath:" + CITIES_DATA_FILENAME)
    private Resource initialDataFile;
}
