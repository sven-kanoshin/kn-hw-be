package ee.proekspert.kn.homework.model;

import java.util.List;

/**
 * Interface for domain objects which participate 
 * in the permission system.
 * This interface is used to communicate the allowed 
 * action to API consumers. 
 */

public interface Permissible {
    
    /**
     * Set allowed actions. 
     * The allowed actions will answer the question: "What the given user can do with the given resource"
     * @param actions
     */
    void setAllowedActions(List<String> actions);
}
