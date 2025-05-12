package greencity.config;

import greencity.filters.CachingFilter;
import greencity.logging.RestLoggingInterceptor;
import greencity.logging.TransactionLoggingIdFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {
    private final RestLoggingInterceptor restLoggingInterceptor;
    private final TransactionLoggingIdFilter transactionLoggingIdFilter;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(restLoggingInterceptor);
    }

    @Bean
    public FilterRegistrationBean<TransactionLoggingIdFilter> transactionIdFilter() {
        FilterRegistrationBean<TransactionLoggingIdFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(transactionLoggingIdFilter);
        registrationBean.addUrlPatterns("/*");
        registrationBean.setOrder(1);
        return registrationBean;
    }

    @Bean
    public CachingFilter cachingFilterBean() {
        return new CachingFilter();
    }

    @Bean
    public FilterRegistrationBean<CachingFilter> cachingFilter() {
        FilterRegistrationBean<CachingFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(cachingFilterBean());
        registrationBean.addUrlPatterns("/*");
        registrationBean.setOrder(2);
        return registrationBean;
    }
}