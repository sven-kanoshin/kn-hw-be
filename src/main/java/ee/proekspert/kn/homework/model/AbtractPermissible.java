package ee.proekspert.kn.homework.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;

import java.util.Collections;
import java.util.List;

/**
 * Adds the "permissions" list to governed entities
 */
public abstract class AbtractPermissible implements Permissible {
    
    @JsonProperty(value = "permissions", access = JsonProperty.Access.READ_ONLY)
    protected List<String> actions = Collections.emptyList(); 
    
    @Override
    @Schema(accessMode =  AccessMode.READ_ONLY)
    public void setAllowedActions(List<String> allowedActions) {
        if ( allowedActions != null )
            this.actions = allowedActions;
    }
}