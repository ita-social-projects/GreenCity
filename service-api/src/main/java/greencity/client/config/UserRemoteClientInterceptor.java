package greencity.client.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import greencity.client.UserRemoteClient;
import greencity.enums.Role;
import greencity.security.jwt.JwtTool;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import java.util.List;

/**
 * Class for setting <em>Authorization</em> header for
 * {@link UserRemoteClient}'s requests.
 *
 * @author Andrii Yezenitskyi
 */
@RequiredArgsConstructor
@Component
@Slf4j
public class UserRemoteClientInterceptor implements RequestInterceptor {
    private final JwtTool jwtTool;
    @Value("${spring.liquibase.parameters.service-email}")
    private String serviceEmail;
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String TOKEN_FORMAT = "Bearer %s";

    /**
     * Sets <em>Authorization</em> header with access token for request.
     *
     * @param template {@link RequestTemplate} of {@link UserRemoteClient}.
     */
    @Override
    public void apply(RequestTemplate template) {
        ServletRequestAttributes servletRequestAttributes =
            ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes());

        String accessToken = servletRequestAttributes != null
            ? servletRequestAttributes.getRequest().getHeader(AUTHORIZATION_HEADER)
            : createAccessTokenForService();

        template.header(AUTHORIZATION_HEADER, accessToken);
    }

    /**
     * Creates JWT for service-to-service communication.
     *
     * @return {@link String} - access token.
     */
    private String createAccessTokenForService() {
        List<Role> roles = List.of(Role.ROLE_USER, Role.ROLE_ADMIN);
        return String.format(TOKEN_FORMAT, jwtTool.createAccessToken(serviceEmail, roles));
    }
}
