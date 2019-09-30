package greencity.config;

import greencity.security.jwt.JwtFilter;
import greencity.security.jwt.JwtTokenTool;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Config for JWT security.
 *
 * @author Nazar Stasyuk
 * @version 1.0
 */
public class JwtConfig extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {
    private JwtTokenTool tool;

    /**
     * Constructor.
     *
     * @param tool {@link JwtTokenTool} - tool for JWT
     */
    public JwtConfig(JwtTokenTool tool) {
        this.tool = tool;
    }

    /**
     * Method that add Jwt filter before.
     *
     * @param builder {@link HttpSecurity}
     */
    @Override
    public void configure(HttpSecurity builder) throws Exception {
        JwtFilter filter = new JwtFilter(tool);
        builder.addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class);
    }
}
