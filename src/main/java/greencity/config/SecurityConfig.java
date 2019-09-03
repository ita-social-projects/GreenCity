package greencity.config;

import greencity.security.JwtTokenTool;
import java.util.Arrays;
import java.util.Collections;

import greencity.security.JwtTokenTool;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
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

    /**
     * Constructor.
     *
     * @param tool {@link JwtTokenTool} - tool for JWT
     */
    public SecurityConfig(JwtTokenTool tool) {
        this.tool = tool;
    }

    /** Bean {@link PasswordEncoder} that uses in coding password. */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /** Bean {@link AuthenticationManager} that uses in authentication managing. */
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
    /**
     * Method for configure security
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
            .antMatchers("/ownSecurity/**")
            .permitAll()
            .antMatchers("/place/getListPlaceLocationByMapsBounds/**")
            .permitAll()
            .antMatchers("/place/propose/**")
            .hasAnyRole("USER", "ADMIN", "MODERATOR")
            .antMatchers("/place/{status}")
            .hasAnyRole("USER", "ADMIN", "MODERATOR")
            .antMatchers("/place/status**")
            .hasAnyRole("ADMIN", "MODERATOR")
            .antMatchers("/favorite_place/**")
            .hasAnyRole("USER", "ADMIN", "MODERATOR")
            .antMatchers("/place/save/favorite_place")
            .hasAnyRole("USER", "ADMIN", "MODERATOR")
            .antMatchers("/place/info/favorite")
            .hasAnyRole("USER", "ADMIN", "MODERATOR")
            .antMatchers("/user/role/**")
            .hasAnyRole("USER", "ADMIN", "MODERATOR")
            .anyRequest()
            .hasAnyRole("ADMIN")
            .and()
            .apply(new JwtConfig(tool));
    }

    /** Bean {@link CorsConfigurationSource} that uses for CORS setup. */
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

    /**
     * Method for configure matchers that will be ignored in security
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
}
