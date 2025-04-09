package greencity.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuration Swagger into Green City project.
 *
 * @author Kateryna Horokh
 */
@Configuration
@Slf4j
public class SwaggerConfig {
    /**
     * Customizing the OpenAPI bean.
     *
     * @return {@link OpenAPI}
     */
    @Bean
    public OpenAPI customizeOpenAPI() {
        final String securitySchemeName = "bearerAuth";
        return new OpenAPI()
            .servers(List.of(new Server().url("/api/greencity")))
            .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
            .components(new Components().addSecuritySchemes(securitySchemeName,
                new SecurityScheme()
                    .name(securitySchemeName)
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("bearer")
                    .bearerFormat("JWT")))
            .info(new Info().title("Greencity API")
                .summary("Api Documentation")
                .version("3.0.3")
                .license(new License().name("Apache 2.0").identifier("Apache-2.0")
                    .url("https://www.apache.org/licenses/LICENSE-2.0.html")))
            .openapi("3.0.3");
    }
}
