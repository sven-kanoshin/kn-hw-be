package ee.proekspert.kn.homework;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.csv.CsvGenerator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.datatype.jsr310.ser.ZonedDateTimeSerializer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@EnableScheduling
@SpringBootApplication
@EnableJpaRepositories("ee.proekspert.kn")
@EnableAutoConfiguration // (exclude = {DataSourceAutoConfiguration.class})
@EnableTransactionManagement
public class HomeworkApplication {

    public static final String ZONEDDATETIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZ";

    public static void main(String[] args) {
        SpringApplication.run(HomeworkApplication.class, args);
    }

    @Bean
    @Primary
    // Manually re-defined object mapper, set as primary
    // this solves a conflict between the json and csv objectmappers
    protected ObjectMapper objectMapper() {
        return Jackson2ObjectMapperBuilder.json()
                .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .featuresToDisable(SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS)
                .serializerByType(ZonedDateTime.class, new ZonedDateTimeSerializer(DateTimeFormatter.ofPattern(ZONEDDATETIME_FORMAT)))
                .build();
    }

    @Bean
    protected CsvMapper csvMapper() {
        return CsvMapper.builder()
                .enable(CsvGenerator.Feature.ALWAYS_QUOTE_STRINGS)
                .configure(MapperFeature.ALLOW_COERCION_OF_SCALARS, true)
                .build();
    }
}
