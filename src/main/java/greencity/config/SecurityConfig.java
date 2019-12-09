package greencity.config;

import static greencity.constant.AppConstant.*;
import static javax.servlet.http.HttpServletResponse.SC_FORBIDDEN;
import static javax.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;

import greencity.security.filters.AccessTokenAuthenticationFilter;
import greencity.security.jwt.JwtTool;
import greencity.security.providers.JwtAuthenticationProvider;
import java.util.Arrays;
import java.util.Collections;
import static javax.servlet.http.HttpServletResponse.SC_FORBIDDEN;
import static javax.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * Config for security.
 *
 * @author Nazar Stasyuk && Yurii Koval
 * @version 1.0
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private final JwtTool jwtTool;

    /**
     * Constructor.
     */
    @Autowired
    public SecurityConfig(JwtTool jwtTool) {
        this.jwtTool = jwtTool;
    }

    /**
     * Bean {@link PasswordEncoder} that uses in coding password.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Method for configure security.
     *
     * @param http {@link HttpSecurity}
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors()
            .and()
            .csrf()
            .disable()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .addFilterBefore(
                new AccessTokenAuthenticationFilter(jwtTool, authenticationManager()),
                UsernamePasswordAuthenticationFilter.class
            )
            .exceptionHandling()
            .authenticationEntryPoint((req, resp, exc) -> resp.sendError(SC_UNAUTHORIZED, "Authorize first."))
            .accessDeniedHandler((req, resp, exc) -> resp.sendError(SC_FORBIDDEN, "You don't have authorities."))
            .and()
            .authorizeRequests()
            .antMatchers(
                "/ownSecurity/**",
                "/place/getListPlaceLocationByMapsBounds/**",
                "/googleSecurity/**",
                "/place/filter/**",
                "/restorePassword/**",
                "/changePassword/**"
            ).permitAll()
            .antMatchers(
                HttpMethod.GET,
                "/category/**",
                "/place/info/{id}/**",
                "/favorite_place/favorite/{id}",
                "/place/info/favorite/**",
                "/place/statuses/**",
                "/user/emailNotifications/**",
                "/place/about/{id}/**",
                "/specification/**"
            ).permitAll()
            .antMatchers(HttpMethod.GET,
                "/advices/random/*",
                "/facts/random/*",
                "/habit/statistic/*",
                "/user/{userId}/habits",
                "/user/{userId}/habits/statistic",
                "/user/{userId}/goals",
                "/user/{userId}/goals/*",
                "/user/{userId}/habit-dictionary/available",
                "/user/{userId}/customGoals",
                "/user/{userId}/customGoals/*"
            ).hasAnyRole(USER, ADMIN, MODERATOR)
            .antMatchers(
                "/place/propose/**",
                "/place/{status}/**",
                "/favorite_place/**",
                "/place/save/favorite",
                "/user"
            ).hasAnyRole(USER, ADMIN, MODERATOR)
            .antMatchers(HttpMethod.PATCH,
                "/habit/statistic/*",
                "/user/{userId}/goals/*",
                "/user/{userId}/customGoals"
            ).hasAnyRole(USER, ADMIN, MODERATOR)
            .antMatchers(HttpMethod.POST,
                "/category/**",
                "/place/save/favorite/**",
                "/habit/statistic/",
                "/user/{userId}/goals",
                "/user/{userId}/habits",
                "/user/{userId}/habit",
                "/user/{userId}/habits/statistic",
                "/user/{userId}/goals/*",
                "/user/{userId}/habit-dictionary/available"
                "/user/{userId}/goals",
                "/user/{userId}/customGoals"
            ).hasAnyRole(USER, ADMIN, MODERATOR)
            .antMatchers(HttpMethod.DELETE,
                "/user/{userId}/customGoals",
                "/user/{userId}/userGoals"
            ).hasAnyRole(USER, ADMIN, MODERATOR)
            .antMatchers(HttpMethod.DELETE,
                "/user/{userId}/habit/{habitId}"
            ).hasAnyRole(USER,ADMIN, MODERATOR)
            .antMatchers(HttpMethod.POST,
                "/user/filter",
                "/place/filter/predicate"
            ).hasAnyRole(ADMIN, MODERATOR)
            .antMatchers("/advices/*", "/facts/*").hasAnyRole(ADMIN, MODERATOR)
            .antMatchers(HttpMethod.PATCH,
                "/place/status**",
                "/place/statuses**",
                "/user/update/status"
            ).hasAnyRole(ADMIN, MODERATOR)
            .antMatchers(HttpMethod.PATCH,
                "/user/update/role"
            ).hasRole(ADMIN)
            .antMatchers(HttpMethod.GET,
                "/user/all/",
                "/user/roles",
                "/comments"
            ).hasAnyRole(ADMIN, MODERATOR)
            .antMatchers(HttpMethod.DELETE,
                "/place/{id}/**",
                "/place/**",
                "/comments"
            ).hasAnyRole(ADMIN, MODERATOR)
            .antMatchers(HttpMethod.PUT,
                "/user/**",
                "/ownSecurity/**"
            ).hasAnyRole(USER, ADMIN, MODERATOR)
            .antMatchers(HttpMethod.PUT,
                "/place/update/**"
            ).hasAnyRole(ADMIN, MODERATOR)
            .antMatchers(HttpMethod.PATCH,
                "/user/update/role"
            ).hasRole(ADMIN)
            .anyRequest().hasAnyRole(ADMIN);
    }

    /**
     * Method for configure matchers that will be ignored in security.
     *
     * @param web {@link WebSecurity}
     */
    @Override
    public void configure(WebSecurity web) {
        web.ignoring().antMatchers("/v2/api-docs/**");
        web.ignoring().antMatchers("/swagger.json");
        web.ignoring().antMatchers("/swagger-ui.html");
        web.ignoring().antMatchers("/swagger-resources/**");
        web.ignoring().antMatchers("/webjars/**");
    }


    /**
     * Method for configure type of authentication provider.
     *
     * @param auth {@link AuthenticationManagerBuilder}
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(new JwtAuthenticationProvider(jwtTool));
    }

    /**
     * Provides AuthenticationManager.
     *
     * @return {@link AuthenticationManager}
     */
    @Bean
    @Override
    public AuthenticationManager authenticationManager() throws Exception {
        return super.authenticationManager();
    }

    /**
     * Bean {@link CorsConfigurationSource} that uses for CORS setup.
     */
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Collections.singletonList("*"));
        configuration.setAllowedMethods(
            Arrays.asList("GET", "POST", "OPTIONS", "DELETE", "PUT", "PATCH"));
        configuration.setAllowedHeaders(
            Arrays.asList(
                "X-Requested-With", "Origin", "Content-Type", "Accept", "Authorization"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
