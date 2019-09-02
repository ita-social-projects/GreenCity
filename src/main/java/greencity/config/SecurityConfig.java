package greencity.config;

import greencity.security.JwtTokenTool;
import java.util.Arrays;
import java.util.Collections;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private JwtTokenTool tool;

    /**
     * Generated javadoc, must be replaced with real one.
     */
    public SecurityConfig(JwtTokenTool tool) {
        this.tool = tool;
    }

    /**
     * Generated javadoc, must be replaced with real one.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

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
            .antMatchers(HttpMethod.PATCH,"/place/status**")
            .hasAnyRole("ADMIN", "MODERATOR")
            .antMatchers("/place/{status}/**")
            .hasAnyRole("USER", "ADMIN", "MODERATOR")
            .antMatchers("/favoritePlace/**")
            .hasAnyRole("USER", "ADMIN", "MODERATOR")
            .antMatchers("/place/save/favoritePlace")
            .hasAnyRole("USER", "ADMIN", "MODERATOR")
            .anyRequest()
            .hasAnyRole("ADMIN")
            .and()
            .apply(new JwtConfig(tool));
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/v2/api-docs/**");
        web.ignoring().antMatchers("/swagger.json");
        web.ignoring().antMatchers("/swagger-ui.html");
        web.ignoring().antMatchers("/swagger-resources/**");
        web.ignoring().antMatchers("/webjars/**");
    }

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
