package ee.proekspert.kn.homework.security.injector;

import lombok.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import ee.proekspert.kn.homework.model.Permissible;
import ee.proekspert.kn.homework.security.model.AllowedAction;

public abstract class AbstractPermissionInjector implements PermissionInjector {

    private final Class<?> supportedType;
    private final Map<String, AllowedAction> permissionActionMap;
        
    public AbstractPermissionInjector(@NonNull Class<?> type,
            @NonNull Map<String, AllowedAction> permissionsActionMap) {
        this.supportedType = type;
        this.permissionActionMap = permissionsActionMap;
    }
    
    @Override
    public boolean isTypeSupported(Class<?> type) {
        return this.supportedType == type;
    }
    
    @Override
    public void inject(Permissible target) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return;
        }
        Set<String> authorities = AuthorityUtils.authorityListToSet(auth.getAuthorities());
        // Resolve the permissions the given injector manages and calculate
        // if given user has the needer permissions
        List<String> allowed = permissionActionMap.entrySet()
                .stream()
                .map(entry -> authorities.contains(entry.getKey()) ? entry.getValue() : null)
                .filter(Objects::nonNull)
                .map(AllowedAction::name)
                .toList();

        target.setAllowedActions(allowed);
    }
}
