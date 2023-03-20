package ee.proekspert.kn.homework.security.permissions;

import org.springframework.security.access.annotation.Secured;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import ee.proekspert.kn.homework.security.model.AccessPermissions;

@Retention(RetentionPolicy.RUNTIME)
@Secured(AccessPermissions.Cities.ALLOW_EDIT)
public @interface EditCityPermission {
}
