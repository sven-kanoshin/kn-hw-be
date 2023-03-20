package ee.proekspert.kn.homework.cities.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import ee.proekspert.kn.homework.cities.model.City;
import ee.proekspert.kn.homework.cities.repository.CitiesRepository;
import ee.proekspert.kn.homework.model.NotFoundException;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT) 
class CitiesServiceTest {

    private static final long EXISTING_ID = 17L;
    private static final long MID_ID = 13L;
    private static final long END_ID = 11L;
    
    private static final String REF_GOOD = "good-reference";
    private static final String REF_BAD = "bad-reference";
    
    private static final String LONG_NAME = "n".repeat(64);
    private static final String NOT_SO_LONG_NAME = "n".repeat(63);
    
    private static final String LOONG_URL ="u".repeat(2048);
    private static final String NOT_SO_LONG_URL ="u".repeat(2047);
    
    @Mock
    private CitiesRepository repository;
    
    private CitiesServices service;
    
    private final List<City> cityList = List.of(
            City.builder().id(EXISTING_ID).reference(REF_GOOD).build(),
            City.builder().id(MID_ID).reference(REF_GOOD + MID_ID).build(),
            City.builder().id(END_ID).reference(REF_GOOD + END_ID).build()
            );
    
    @BeforeEach
    void setUp() throws Exception {
        service = new CitiesServices(repository);
        
        when(repository.findByReference(REF_GOOD)).thenReturn(City.builder().id(EXISTING_ID).reference(REF_GOOD).build());
        when(repository.findByReference(REF_BAD)).thenReturn(null);
        
        when(repository.findAll(isNull(), any(Sort.class))).thenReturn(cityList);
        when(repository.findAll(any(PageRequest.class))).thenAnswer(i -> {
            Pageable page = (Pageable)i.getArgument(0);
            int startIdx = page.getPageNumber() * page.getPageSize();
            startIdx = Math.min(cityList.size()-1, Math.max(startIdx, 0));
            int endIdx = (page.getPageNumber() + 1) * page.getPageSize();
            endIdx  = Math.min(cityList.size(), Math.max(endIdx, 0));
            return new PageImpl<City>(cityList.subList(startIdx, endIdx), page, cityList.size() );
        });
        
        when(repository.findAll(ArgumentMatchers.<Specification<City>>any(), any(PageRequest.class))).thenAnswer(i -> {
            Pageable page = (Pageable)i.getArgument(1);
            int startIdx = page.getPageNumber() * page.getPageSize();
            startIdx = Math.min(cityList.size()-1, Math.max(startIdx, 0));
            int endIdx = (page.getPageNumber() + 1) * page.getPageSize();
            endIdx  = Math.min(cityList.size(), Math.max(endIdx, 0));
            return new PageImpl<City>(cityList.subList(startIdx, endIdx), page, cityList.size() );
        });
        when(repository.findAll(ArgumentMatchers.<Specification<City>>any(), any(Sort.class))).thenReturn(cityList);
        
        when(repository.save(any(City.class))).thenAnswer(i -> i.getArgument(0));
    }
        
    @Nested
    class Update 
    {
        @Test
        void nullReference() {
            // The repository might handle the 'null' as reference and return unexpected data
            // We don't want that, need to check that our service catches that before
            assertThrows(NullPointerException.class, () -> service.update(null, City.builder().build()));
        }       
        
        @Test
        void nullCity() {
            assertThrows(NullPointerException.class, () -> service.update(REF_GOOD, null));
        }
        
        @Test
        void unknownCity() {
            assertThrows(NotFoundException.class, () -> service.update(REF_BAD, City.builder().build()));
        }
        
        @Test
        void longName() {
            ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> service.update(REF_GOOD, City.builder().name(LONG_NAME).imgUrl("").build()));
            assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
        }
        
        @Test
        void longUrl() {
            ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> service.update(REF_GOOD, City.builder().name("").imgUrl(LOONG_URL).build()));
            assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
        }
        
        @Test
        void happy() {
            City result = service.update(REF_GOOD, City.builder()
                    .name(NOT_SO_LONG_NAME)
                    .imgUrl(NOT_SO_LONG_URL)
                    .build());
            assertAll(
                    () -> assertEquals(EXISTING_ID, result.getId()), 
                    () -> assertEquals(REF_GOOD, result.getReference()),
                    () -> assertEquals(NOT_SO_LONG_NAME, result.getName()), 
                    () -> assertEquals(NOT_SO_LONG_URL, result.getImgUrl()));
        }
    }
    
    @Nested 
    class GetList {
        
        @Test
        void emptySize() {
            Page<City> result = service.getList(Optional.empty(), Optional.empty());
            
            assertThat(result.getContent(), hasItems(cityList.toArray(new City[0])));
        }
        
        @Test
        void limitSize() {
            Page<City> result = service.getList(Optional.empty(), Optional.of(1));
            assertAll(
                    () -> assertEquals(1, result.getContent().size()), 
                    () -> assertEquals(1, result.getSize()), 
                    () -> assertEquals(cityList.size(), result.getTotalPages()),
                    () -> assertEquals(0, result.getNumber()), 
                    () -> assertEquals(cityList.size(), result.getTotalElements()),
                    () -> assertEquals(REF_GOOD, result.getContent().get(0).getReference()));
        }
        
        
        @Test
        void limitPage() {
            Page<City> result = service.getList(Optional.of(2), Optional.of(1));
            assertAll(
                    () -> assertEquals(1, result.getContent().size()), 
                    () -> assertEquals(1, result.getSize()), 
                    () -> assertEquals(cityList.size(), result.getTotalPages()),
                    () -> assertEquals(2, result.getNumber()), 
                    () -> assertEquals(cityList.size(), result.getTotalElements()),
                    () -> assertEquals(REF_GOOD + END_ID, result.getContent().get(0).getReference() ));
        }
    }
    
    @Nested 
    class Search {
        
        @Test
        void nullSearch() {
            // 'null' might be translated by the underlying repository in an unknown way
            // Don't want that, need to catch it by ourselves
            assertThrows(NullPointerException.class, () -> service.search(null, Optional.empty(), Optional.empty()));
        }
        
        @Test
        void emptySearch() {
            Page<City> result = service.search("", Optional.empty(), Optional.empty());
            assertThat(result.getContent(), hasItems(cityList.toArray(new City[0])));
        }
        
        @Test
        void limitSize() {
            Page<City> result = service.search("stuff", Optional.empty(), Optional.of(1));
            // Note: We do not assert search string, 
            // since this is the responsibility of the DB and not under test here 
            assertAll(
                    () -> assertEquals(1, result.getContent().size()), 
                    () -> assertEquals(1, result.getSize()), 
                    () -> assertEquals(cityList.size(), result.getTotalPages()),
                    () -> assertEquals(0, result.getNumber()), 
                    () -> assertEquals(cityList.size(), result.getTotalElements()),
                    () -> assertEquals(REF_GOOD, result.getContent().get(0).getReference()));
        }
        
        @Test
        void limitPage() {
            // Note: We do not assert search string, 
            // since this is the responsibility of the DB and not under test here             
            Page<City> result = service.search("stuff", Optional.of(2), Optional.of(1));            
            assertAll(
                    () -> assertEquals(1, result.getContent().size()), 
                    () -> assertEquals(1, result.getSize()), 
                    () -> assertEquals(cityList.size(), result.getTotalPages()),
                    () -> assertEquals(2, result.getNumber()), 
                    () -> assertEquals(cityList.size(), result.getTotalElements()),
                    () -> assertEquals(REF_GOOD + END_ID, result.getContent().get(0).getReference() ));            
        }
    }

}
