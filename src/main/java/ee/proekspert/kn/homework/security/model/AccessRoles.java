package ee.proekspert.kn.homework.security.model;

import lombok.Getter;

/**
 * Roles and Permissions definitions. Currently hardcoded, but can be
 * integrated with a permission provider system
 */
public class AccessRoles {

    public static final AccessRoles ANON = new AccessRoles(
            );
    
    public static final AccessRoles USER = new AccessRoles(
            AccessPermissions.Cities.ALLOW_VIEW
            );
    
    public static final AccessRoles ADMIN = new AccessRoles(
            AccessPermissions.Cities.ALLOW_VIEW,
            AccessPermissions.Cities.ALLOW_EDIT
            );
    
    @Getter
    private final String[] permissions;
    
    private AccessRoles(String... permissions) {
        this.permissions = permissions;
    }
}
