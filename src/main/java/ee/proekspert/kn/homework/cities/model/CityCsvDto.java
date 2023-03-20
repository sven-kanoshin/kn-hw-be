package ee.proekspert.kn.homework.cities.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;

/**
 * DTO for initial loading of cities from .csv file
 */
@Getter
@Builder
@ToString
@Jacksonized
@RequiredArgsConstructor
public class CityCsvDto {
    
    @JsonProperty("id")
    private final long id;
    
    @JsonProperty("name")
    private final String name;
    
    @JsonProperty("photo")
    private final String imgUrl;
}
