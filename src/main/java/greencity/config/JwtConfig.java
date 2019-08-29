package greencity.config;

import javax.sql.DataSource;

import greencity.security.JwtFilter;
import greencity.security.JwtTokenTool;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

public class JwtConfig extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    private JwtTokenTool tool;

    public JwtConfig(JwtTokenTool tool) {
        this.tool = tool;
    }

    @Override
    public void configure(HttpSecurity builder) throws Exception {
        JwtFilter filter = new JwtFilter(tool);
        builder.addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class);
    }
}
