package greencity.config;

import greencity.handler.CustomPageableHandlerMethodArgumentResolver;
import greencity.handler.CustomSortHandlerMethodArgumentResolver;
import greencity.validator.SortPageableValidator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.SortHandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class PageableConfig implements WebMvcConfigurer {
    private final SortPageableValidator sortPageableValidator;

    @Bean
    public SortHandlerMethodArgumentResolver sortHandlerMethodArgumentResolver() {
        return new SortHandlerMethodArgumentResolver();
    }

    @Bean
    public CustomSortHandlerMethodArgumentResolver customSortResolver(
        SortHandlerMethodArgumentResolver sortHandlerMethodArgumentResolver) {
        return new CustomSortHandlerMethodArgumentResolver(
            sortPageableValidator,
            sortHandlerMethodArgumentResolver);
    }

    @Bean
    public CustomPageableHandlerMethodArgumentResolver customPageableResolver(
        CustomSortHandlerMethodArgumentResolver customSortResolver) {
        return new CustomPageableHandlerMethodArgumentResolver(customSortResolver);
    }

    /**
     * Adds custom pageable and sort resolvers to the list of argument resolvers.
     */
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(customPageableResolver(customSortResolver(
            sortHandlerMethodArgumentResolver())));

        WebMvcConfigurer.super.addArgumentResolvers(argumentResolvers);
    }
}