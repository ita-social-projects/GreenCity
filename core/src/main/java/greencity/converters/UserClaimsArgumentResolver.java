package greencity.converters;

import greencity.annotations.CurrentUserClaims;
import greencity.dto.user.UserClaims;
import greencity.security.jwt.JwtTool;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
@RequiredArgsConstructor
public class UserClaimsArgumentResolver implements HandlerMethodArgumentResolver {
    private final JwtTool jwtTool;

    @Override
    public boolean supportsParameter(@NonNull MethodParameter parameter) {
        return parameter.getParameterAnnotation(CurrentUserClaims.class) != null
            && parameter.getParameterType().isAssignableFrom(UserClaims.class);
    }

    @Override
    public UserClaims resolveArgument(@NonNull MethodParameter parameter,
        ModelAndViewContainer mavContainer,
        @NonNull NativeWebRequest webRequest,
        WebDataBinderFactory binderFactory) throws Exception {
        String jwt = jwtTool.extractJwtFromNativeWebRequest(webRequest);
        return jwtTool.extractUserClaims(jwt);
    }
}
