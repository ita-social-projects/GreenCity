package greencity.config;

import greencity.converters.UserArgumentResolver;
import greencity.converters.UserClaimsArgumentResolver;
import greencity.converters.UserIdArgumentResolver;
import greencity.security.jwt.JwtTool;
import greencity.service.UserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import java.util.List;
import java.util.Locale;

/**
 * Web MVC configuration class for the application.
 * <p>
 * This class configures essential MVC components, such as:
 * <ul>
 * <li>Custom pageable and sort argument resolvers for pagination and
 * sorting.</li>
 * <li>Locale resolution and switching based on request parameters.</li>
 * <li>Validation message source for internationalization of validation
 * messages.</li>
 * <li>Multipart file upload support using
 * {@link StandardServletMultipartResolver}.</li>
 * <li>Custom argument resolvers for injecting user data into controller
 * methods.</li>
 * </ul>
 * </p>
 * Implements {@link WebMvcConfigurer} to customize Spring MVC configuration.
 */
@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {
    /**
     * Custom pageable argument resolver for handling pagination in API requests.
     */
    private final CustomPageableHandlerMethodArgumentResolver customPageableArgumentResolver;

    /** Custom sort argument resolver for handling sorting in API requests. */
    private final CustomSortHandlerMethodArgumentResolver customSortHandlerMethodArgumentResolver;

    /**
     * Service for user-related operations, used in {@link UserArgumentResolver}.
     */
    private final UserService userService;
    private final ModelMapper modelMapper;
    private final JwtTool jwtTool;

    /**
     * Configures the message source for internationalization of application
     * messages.
     *
     * @return a {@link MessageSource} configured with UTF-8 encoding and message
     *         bundle location
     */
    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:messages");
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }

    /**
     * Provides the validator bean configured with the message source for i18n.
     *
     * @return a {@link LocalValidatorFactoryBean} used for bean validation
     */
    @Bean
    @Override
    public LocalValidatorFactoryBean getValidator() {
        LocalValidatorFactoryBean bean = new LocalValidatorFactoryBean();
        bean.setValidationMessageSource(messageSource());
        return bean;
    }

    /**
     * Configures the locale resolver to determine the current locale.
     *
     * @return a {@link SessionLocaleResolver} with default locale set to English
     */
    @Bean
    public LocaleResolver localeResolver() {
        SessionLocaleResolver localeResolver = new SessionLocaleResolver();
        localeResolver.setDefaultLocale(Locale.ENGLISH);
        return localeResolver;
    }

    /**
     * Configures an interceptor to switch the locale based on the "lang" request
     * parameter.
     *
     * @return a {@link LocaleChangeInterceptor} for handling dynamic locale changes
     */
    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor lci = new LocaleChangeInterceptor();
        lci.setParamName("lang");
        return lci;
    }

    /**
     * Configures multipart file upload support using the standard servlet multipart
     * resolver.
     *
     * @return a {@link MultipartResolver} capable of handling file uploads
     */
    @Bean
    public MultipartResolver multipartResolver() {
        return new StandardServletMultipartResolver();
    }

    /**
     * Registers interceptors for the application.
     * <p>
     * Currently, this adds the {@link LocaleChangeInterceptor} to allow switching
     * the locale via the "lang" request parameter.
     * </p>
     *
     * @param registry the {@link InterceptorRegistry} to which interceptors are
     *                 added
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(localeChangeInterceptor());
    }

    /**
     * Configures custom argument resolvers for controller method parameters.
     * <p>
     * Replaces the default {@link PageableHandlerMethodArgumentResolver} with
     * custom pageable and sort resolvers. Also adds a {@link UserArgumentResolver}
     * to inject user details.
     * </p>
     *
     * @param resolvers the list of {@link HandlerMethodArgumentResolver} instances
     *                  to configure
     */
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.removeIf(PageableHandlerMethodArgumentResolver.class::isInstance);
        resolvers.add(new UserArgumentResolver(userService, modelMapper));
        resolvers.add(new UserIdArgumentResolver(jwtTool));
        resolvers.add(new UserClaimsArgumentResolver(jwtTool));
        resolvers.add(customSortHandlerMethodArgumentResolver);
        resolvers.add(customPageableArgumentResolver);
    }
}