package ee.proekspert.kn.homework;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Configures endpoint swagger generator
 */
@Configuration
public class SwaggerConfiguration {

    public static final String SWAGGER_SECURITY_SCHEME_NAME = "authToken";
    
    @Bean
    public GroupedOpenApi api() {
        return GroupedOpenApi.builder()
                .group("kn-homework-api")
                .packagesToScan()
                .pathsToMatch("/api/**")
                .addOpenApiCustomizer(apiCustomizer())
                .build();
    }

    @Bean
    public OpenAPI apiInfo() {
        return new OpenAPI()
                .info(new Info()
                        .title("Kuehne + Nagel: Homework API")
                        .description("Kuehne + Nagel: Homework RESTful API")
                        .version("v" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM")))
                        .contact(new Contact()
                                .name("AS Proekspert")
                                .url("https://www.proekspert.com")
                                .email("info@proekspert.com")))
                .components(new Components()
                        .addSecuritySchemes(SWAGGER_SECURITY_SCHEME_NAME,
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.APIKEY).name("Authorization")
                                        .scheme("bearer").bearerFormat("Auth Token")
                                        .description("Token from Login")
                                        .in(SecurityScheme.In.HEADER)));
    }

    private OpenApiCustomizer apiCustomizer() {
        return this::setCommonResponses;
    }

    private void setCommonResponses(OpenAPI openApi) {
        ApiResponse internalServerErrorResponse = new ApiResponse()
                .description("Internal exception")
                .content(new Content());

        openApi.getPaths().values().forEach(pathItem -> pathItem.readOperations().forEach(operation -> {
            operation.getResponses().addApiResponse("500", internalServerErrorResponse);
        }));
    }
}
