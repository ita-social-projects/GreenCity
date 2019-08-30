package greencity.config;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.ResponseEntity;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.awt.print.Pageable;
import java.util.Date;
import java.util.List;

import static springfox.documentation.builders.PathSelectors.regex;

@Configuration
@EnableSwagger2
@Import(springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration.class)
@Slf4j
public class SwaggerConfig {
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String DEFAULT_INCLUDE_PATTERN = "/.*";

    @Bean
    public Docket swaggerSpringfoxDocket() {
        log.info("Starting Swagger");

        Docket docket =
                new Docket(DocumentationType.SWAGGER_2)
                        .pathMapping("/")
                        .apiInfo(ApiInfo.DEFAULT)
                        .forCodeGeneration(true)
                        .genericModelSubstitutes(ResponseEntity.class)
                        .ignoredParameterTypes(Pageable.class)
                        .ignoredParameterTypes(java.sql.Date.class)
                        .directModelSubstitute(java.time.LocalDate.class, java.sql.Date.class)
                        .directModelSubstitute(java.time.ZonedDateTime.class, Date.class)
                        .directModelSubstitute(java.time.LocalDateTime.class, Date.class)
                        .securityContexts(Lists.newArrayList(securityContext()))
                        .securitySchemes(Lists.newArrayList(apiKey()))
                        .useDefaultResponseMessages(false);

        docket = docket.select().paths(regex(DEFAULT_INCLUDE_PATTERN)).build();
        return docket;
    }

    private ApiKey apiKey() {
        return new ApiKey("JWT", AUTHORIZATION_HEADER, "header");
    }

    private SecurityContext securityContext() {
        return SecurityContext.builder()
                .securityReferences(defaultAuth())
                .forPaths(regex(DEFAULT_INCLUDE_PATTERN))
                .build();
    }

    List<SecurityReference> defaultAuth() {
        AuthorizationScope authorizationScope =
                new AuthorizationScope("global", "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        return Lists.newArrayList(new SecurityReference("JWT", authorizationScopes));
    }
}
