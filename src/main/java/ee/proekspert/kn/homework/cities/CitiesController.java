package ee.proekspert.kn.homework.cities;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

import ee.proekspert.kn.homework.cities.model.City;
import ee.proekspert.kn.homework.cities.service.CitiesServices;
import ee.proekspert.kn.homework.model.EnvelopedResponse;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(path = CitiesController.PATH)
@RequiredArgsConstructor
public class CitiesController {
 
    public static final String PATH = "/api/cities";
    
    private final CitiesServices service;
    
    @Operation(summary = "Get list of Cities",
            description = "Supports pagination and fuzzy name search. Requires 'VIEW' permissions"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok",
                    content = {@Content(mediaType = APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = City.class)))})
    })    
    @GetMapping
    public EnvelopedResponse<?> getPaged(
            @RequestParam("page") Optional<Integer> page, 
            @RequestParam("size") Optional<Integer> size,
            @RequestParam("query") Optional<String> query) {
        
        Page<City> cityPage;

        if (query.isPresent() && !query.get().isEmpty()) {
            cityPage = service.search(query.get(), page, size);
        }
        else {
            cityPage = service.getList(page, size);
        }
        
        return EnvelopedResponse.builder()
                .payload(cityPage.getContent())
                .itemCount(cityPage.getNumberOfElements())
                .pageNumber(cityPage.getNumber())
                .pageSize(cityPage.getSize())
                .totalPages(cityPage.getTotalPages())
                .totalSize(cityPage.getTotalElements())
                .build();        
    }
    
    @Operation(summary = "Modify an existing city",
            description = "Requires 'EDIT' permissions"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok",
                    content = {@Content(mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = City.class))}),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
            @ApiResponse(responseCode = "404", description = "Not Found", content = @Content),
    }) 
    @PutMapping("/{reference}")
    public EnvelopedResponse<?> update(@PathVariable String reference, @RequestBody City city) {
        return EnvelopedResponse.builder()
                .payload(service.update(reference, city))
                .itemCount(1)
                .pageNumber(0)
                .pageSize(1)
                .totalPages(1)
                .totalSize(1L)
                .build();
    }  
}
