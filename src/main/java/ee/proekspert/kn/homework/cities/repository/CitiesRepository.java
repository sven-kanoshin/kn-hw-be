package ee.proekspert.kn.homework.cities.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import ee.proekspert.kn.homework.cities.model.City;

/**
 * Database backed repository
 */
@Repository
public interface CitiesRepository extends CrudRepository<City, Long>, JpaSpecificationExecutor<City> {    
    Page<City> findAll(Pageable pageable);
    
    City findByReference(String reference);
}
