package ee.proekspert.kn.homework.cities.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import ee.proekspert.kn.homework.cities.model.City;
import ee.proekspert.kn.homework.cities.repository.CitiesRepository;
import ee.proekspert.kn.homework.model.NotFoundException;
import ee.proekspert.kn.homework.security.permissions.EditCityPermission;
import ee.proekspert.kn.homework.security.permissions.ViewCityPermission;

@Service
@RequiredArgsConstructor
public class CitiesServices {
    
    private static final int NAME_LENGTH_MAX = 63;
    private static final int URL_LENGTH_MAX = 2047;
    
    // Overall default sorting order; needed for idx based pagination
    private static final Sort DEFAULT_SORT = Sort.by(Sort.Direction.ASC, City.COL_NAME);

    private final CitiesRepository repository;
    
    /**
     * Fuzzy search by name. Uses DB native "LIKE" query 
     * @return
     */
    
    @ViewCityPermission
    public Page<City> search(@NonNull String query, @NonNull Optional<Integer> page, @NonNull Optional<Integer> size) {
        if (size.isPresent()) {
            return this.repository.findAll(this.filterByName(query), buildPageRequest(page.orElse(0), size.get()));
        }
        else {
            return new PageImpl<>(this.repository.findAll(this.filterByName(query), DEFAULT_SORT));
        }
    }
    
    @ViewCityPermission
    public Page<City> getList(@NonNull Optional<Integer> page, @NonNull Optional<Integer> size) {
        if ( size.isEmpty() ) 
            return new PageImpl<>(this.repository.findAll(null, DEFAULT_SORT));
        
        return this.repository.findAll(buildPageRequest(page.orElse(0), size.get()));
    }

    @EditCityPermission
    public City update(@NonNull String reference, @NonNull City city) {
        City entity = this.repository.findByReference(reference);
        
        if ( entity == null ) {
            throw new NotFoundException(City.class, reference);
        }
        
        // Manual validation
        // Can be automated later on
        if ( city.getName().length() > NAME_LENGTH_MAX) {
            throw buildBadRequestException("Name too long");
        }
        if ( city.getImgUrl().length() > URL_LENGTH_MAX) {
            throw buildBadRequestException("Image URL too long");
        }
        
        entity = entity.toBuilder()
                .name(city.getName())
                .imgUrl(city.getImgUrl())
                .build();
        
        return this.repository.save(entity);
    }

    private ResponseStatusException buildBadRequestException(String reason) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, reason);
    }
    
    private PageRequest buildPageRequest(int page, int size) {
        return PageRequest.of(page, size, DEFAULT_SORT);
    }
            
    private Specification<City> filterByName(String name) {
        return Specification.where((r, q, cb) -> cb.like(cb.lower(r.get(City.COL_NAME)),"%" + name +"%"));
    }
}
