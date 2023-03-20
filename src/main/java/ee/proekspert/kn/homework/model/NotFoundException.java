package ee.proekspert.kn.homework.model;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;


/**
 * Lookup failure
 * Will cause an HTTP 404(Not Found) error
 */

public class NotFoundException extends ResponseStatusException {
    private static final long serialVersionUID = -8815988904005323985L;
    
    @Getter
    private final Class<?> entity;
   
    public NotFoundException(Class<?> entityClass, String identifier) {
        super(HttpStatus.NOT_FOUND, buildMessage(entityClass, identifier));
        this.entity = entityClass;
    }
    
    private static String buildMessage(Class<?> entityClass, String identifier) {
        return "Entity "+(entityClass==null?"":"'"+entityClass.getSimpleName().toLowerCase()+"'")+" not found with identifier '" + identifier+"'";
    }
}
