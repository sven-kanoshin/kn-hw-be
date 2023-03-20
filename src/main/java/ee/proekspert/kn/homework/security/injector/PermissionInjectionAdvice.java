package ee.proekspert.kn.homework.security.injector;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.util.ArrayList;
import java.util.List;

import ee.proekspert.kn.homework.model.EnvelopedResponse;
import ee.proekspert.kn.homework.model.Permissible;

/**
 * Responsible for collecting permissions for domain objects and injecting them
 * into the output model.
 */
@Slf4j
@RestControllerAdvice
public class PermissionInjectionAdvice implements ResponseBodyAdvice<EnvelopedResponse<?>> {

    private final List<PermissionInjector> permissionInjectors;

    public PermissionInjectionAdvice(List<PermissionInjector> permissionInjectors) {
        if (permissionInjectors == null)
            permissionInjectors = new ArrayList<>();
        this.permissionInjectors = permissionInjectors;
    }

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        // That's the most specific check we can make :/
        return (returnType.getParameterType() == EnvelopedResponse.class);
    }

    @Override
    public EnvelopedResponse<?> beforeBodyWrite(EnvelopedResponse<?> body, MethodParameter returnType, MediaType selectedContentType,
            Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request,
            ServerHttpResponse response) {
        Object payload = body.getPayload();
        if (payload instanceof Permissible) {
            processSingleObject((Permissible)payload);
        }
        if (payload instanceof List && ((List<?>) payload).size() > 0) {
            processList((List<?>) payload);
        }
        return body;
    }

    private void processList(List<?> body) {
        try {
        // Assume all elements are of the same type;
        // find the injector via first element
        PermissionInjector injector = this.findInjector(body.get(0).getClass());

        body.stream()
                .filter(obj -> obj instanceof Permissible)
                .forEach(obj -> injector.inject((Permissible) obj));
        }
        finally {
            log.debug("Resolving permissions for '{}' entities of type '{}'",
                    body.size(), body.get(0).getClass().getSimpleName());
        }
    }

    private void processSingleObject(Permissible body) {
        this.findInjector(body.getClass()).inject(body);
    }

    private PermissionInjector findInjector(Class<?> bodyClass) {
        for (PermissionInjector injector : this.permissionInjectors)
            if (injector.isTypeSupported(bodyClass))
                return injector;
        return PermissionInjector.NOP_INJECTOR;
    }

}
