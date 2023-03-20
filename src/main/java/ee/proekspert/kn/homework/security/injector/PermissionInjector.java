package ee.proekspert.kn.homework.security.injector;

import ee.proekspert.kn.homework.model.Permissible;

/**
 * Interface for services, that evaluates allowed action and 
 * injects them into the given domain objects when 
 * they are sent to output.
 */
public interface PermissionInjector {
    
    public PermissionInjector NOP_INJECTOR = new PermissionInjector() {
        @Override
        public boolean isTypeSupported(Class<?> type) {
            return false;
        }
        
        @Override
        public void inject(Permissible object) {
        }
    };
    
    boolean isTypeSupported(Class<?> type);
    
    void inject(Permissible object);
}
