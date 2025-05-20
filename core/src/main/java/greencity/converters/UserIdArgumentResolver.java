package greencity.converters;

import greencity.annotations.CurrentUserId;
import greencity.exception.exceptions.NoJwtException;
import greencity.security.jwt.JwtTool;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserIdArgumentResolver implements HandlerMethodArgumentResolver {
    private final JwtTool jwtTool;

    @Override
    public boolean supportsParameter(@NonNull MethodParameter parameter) {
        return parameter.getParameterAnnotation(CurrentUserId.class) != null
            && parameter.getParameterType().isAssignableFrom(Long.class);
    }

    @Override
    public Object resolveArgument(@NonNull MethodParameter parameter,
        ModelAndViewContainer mavContainer,
        @NonNull NativeWebRequest webRequest,
        WebDataBinderFactory binderFactory) {
        String jwt;
        try {
            jwt = jwtTool.extractJwtFromNativeWebRequest(webRequest);
        } catch (NoJwtException e) {
            return null;
        }
        return jwtTool.extractUserId(jwt);
    }
}
