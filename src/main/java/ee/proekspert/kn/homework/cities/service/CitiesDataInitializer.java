package ee.proekspert.kn.homework.cities.service;

import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import ee.proekspert.kn.homework.cities.CitiesConfiguration;
import ee.proekspert.kn.homework.cities.model.City;
import ee.proekspert.kn.homework.cities.model.CityCsvDto;
import ee.proekspert.kn.homework.cities.repository.CitiesRepository;

@Slf4j
@Component
@RequiredArgsConstructor
public class CitiesDataInitializer {    
    
    private final CsvMapper csvMapper;
    private final CitiesRepository repository;
    private final CitiesConfiguration config;
    
    
    @EventListener
    public void onApplicationEvent(ContextRefreshedEvent event) {
        log.debug("Before DB load: count {}", repository.count());
        
        if (repository.count() > 0) {
            // Already populated ? Skipping
            log.warn("DB already populated; skipping data init");
            return;
        }
        
        try {
            final CsvSchema schema = csvMapper.schemaFor(CityCsvDto.class)
                    .withLineSeparator(System.lineSeparator())
                    .withHeader();

            final List<City> resultList = new ArrayList<>();
            csvMapper.readerFor(CityCsvDto.class)
                    .with(schema)
                    .<CityCsvDto>readValues(config.getInitialDataFile().getInputStream())
                    .forEachRemaining(item -> resultList.add(this.convertToEntity(item)));

            repository.saveAll(resultList);
        }
        catch (IOException e) {
            throw new RuntimeException("Cannot initiate database", e);
        }
        log.debug("After DB load: count {}", repository.count());
    }
    
    private City convertToEntity(CityCsvDto dto) {
        return City.builder()
                .id(dto.getId())
                .imgUrl(dto.getImgUrl())
                .name(dto.getName())
                .reference(UUID.randomUUID().toString())
                .build();
    }
}
