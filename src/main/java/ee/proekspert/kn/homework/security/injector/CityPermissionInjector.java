package ee.proekspert.kn.homework.security.injector;

import org.springframework.stereotype.Component;

import java.util.Map;

import ee.proekspert.kn.homework.cities.model.City;
import ee.proekspert.kn.homework.security.model.AccessPermissions;
import ee.proekspert.kn.homework.security.model.AllowedAction;

/**
 * Assings allowed actions for City objects
 */
@Component
public class CityPermissionInjector extends AbstractPermissionInjector {

    public CityPermissionInjector() {
        super(City.class, Map.of(AccessPermissions.Cities.ALLOW_EDIT, AllowedAction.EDIT) );
    }
}
