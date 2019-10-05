package greencity.config;

import static greencity.constant.AppConstant.*;

import greencity.security.jwt.JwtAuthenticationProvider;
import greencity.security.jwt.JwtTokenTool;
import greencity.service.UserService;
import java.util.Arrays;
import java.util.Collections;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * Config for security.
 *
 * @author Nazar Stasyuk
 * @version 1.0
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private JwtTokenTool tool;
    private UserService userService;

    /**
     * Constructor.
     *
     * @param tool        {@link JwtTokenTool} - tool for JWT
     * @param userService {@link UserService} - user service.
     */
    public SecurityConfig(JwtTokenTool tool, UserService userService) {
        this.tool = tool;
        this.userService = userService;
    }

    /**
     * Bean {@link PasswordEncoder} that uses in coding password.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Bean {@link AuthenticationManager} that uses in authentication managing.
     */
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
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
            .authorizeRequests()
            .antMatchers(
                "/ownSecurity/**",
                "/place/getListPlaceLocationByMapsBounds/**",
                "/googleSecurity/**",
                "/place/filter/**",
                "/restorePassword/**",
                "/updatePassword/**"
            ).permitAll()
            .antMatchers(
                HttpMethod.GET,
                "/category/**",
                "/place/info/{id}/**",
                "/favorite_place/favorite/{id}",
                "/place/info/favorite/**",
                "/place/statuses/**",
                "/place/about/{id}/**",
                "/specification/**"
            ).permitAll()
            .antMatchers(
                "/place/propose/**",
                "/place/{status}/**",
                "/favorite_place/**",
                "/place/save/favorite"
            ).hasAnyRole(USER, ADMIN, MODERATOR)
            .antMatchers(HttpMethod.POST,
                "/category/**",
                "/place/save/favorite/**"
            ).hasAnyRole(USER, ADMIN, MODERATOR)
            .antMatchers(HttpMethod.POST,
                "/user/filter",
                "/place/filter/predicate"
            ).hasAnyRole(ADMIN, MODERATOR)
            .antMatchers(HttpMethod.PATCH,
                "/place/status**",
                "/place/statuses**",
                "/user/update/status"
            ).hasAnyRole(ADMIN, MODERATOR)
            .antMatchers(HttpMethod.PATCH,
                "/user/update/role"
            ).hasRole(ADMIN)
            .antMatchers(HttpMethod.GET,
                "/user",
                "/user/roles"
            ).hasAnyRole(ADMIN, MODERATOR)
            .antMatchers(HttpMethod.DELETE,
                "/place/{id}/**",
                "/place/**"
            ).hasAnyRole(ADMIN, MODERATOR)
            .anyRequest()
            .hasAnyRole(ADMIN)
            .antMatchers(HttpMethod.PUT,
                "/place/update/**")
            .hasAnyRole(ADMIN, MODERATOR)
            .and()
            .apply(new JwtConfig(tool));
    }

    /**
     * Method for configure matchers that will be ignored in security.
     *
     * @param web {@link WebSecurity}
     */
    @Override
    public void configure(WebSecurity web) throws Exception {
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
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(new JwtAuthenticationProvider(userService, passwordEncoder()));
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
