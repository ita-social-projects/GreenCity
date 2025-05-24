package greencity.converters;

import greencity.ModelUtils;
import greencity.annotations.CurrentUserClaims;
import greencity.dto.user.UserClaims;
import greencity.exception.exceptions.NoJwtException;
import greencity.security.jwt.JwtTool;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;
import java.lang.annotation.Annotation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserClaimsArgumentResolverTest {

    @Mock
    JwtTool jwtTool;

    @InjectMocks
    UserClaimsArgumentResolver userClaimsArgumentResolver;

    @Test
    @SneakyThrows
    void resolveArgumentTest() {
        String jwt = "jwt";
        UserClaims userClaims = ModelUtils.getUserClaims();
        MethodParameter methodParameter = mock(MethodParameter.class);
        ModelAndViewContainer modelAndViewContainer = mock(ModelAndViewContainer.class);
        NativeWebRequest nativeWebRequest = mock(NativeWebRequest.class);
        WebDataBinderFactory webDataBinderFactory = mock(WebDataBinderFactory.class);

        when(jwtTool.extractJwtFromNativeWebRequest(nativeWebRequest))
                .thenReturn(jwt);
        when(jwtTool.extractUserClaims(jwt))
                .thenReturn(userClaims);

        UserClaims actualResult = userClaimsArgumentResolver.resolveArgument(
                methodParameter,
                modelAndViewContainer,
                nativeWebRequest,
                webDataBinderFactory
        );

        assertEquals(userClaims, actualResult);
        verify(jwtTool).extractJwtFromNativeWebRequest(any(NativeWebRequest.class));
        verify(jwtTool).extractUserClaims(jwt);
    }

    @Test
    @SneakyThrows
    void resolveArgumentWhenNoJwtTest() {
        String jwt = "jwt";
        MethodParameter methodParameter = mock(MethodParameter.class);
        ModelAndViewContainer modelAndViewContainer = mock(ModelAndViewContainer.class);
        NativeWebRequest nativeWebRequest = mock(NativeWebRequest.class);
        WebDataBinderFactory webDataBinderFactory = mock(WebDataBinderFactory.class);

        when(jwtTool.extractJwtFromNativeWebRequest(nativeWebRequest))
                .thenThrow(new NoJwtException());

        assertThrows(
                NoJwtException.class,
                () -> userClaimsArgumentResolver.resolveArgument(
                        methodParameter,
                        modelAndViewContainer,
                        nativeWebRequest,
                        webDataBinderFactory
                )
        );

        verify(jwtTool).extractJwtFromNativeWebRequest(any(NativeWebRequest.class));
        verify(jwtTool, never()).extractUserId(jwt);
    }

    private CurrentUserClaims buildCurrentUserClaims() {
        return new CurrentUserClaims() {
            @Override
            public Class<? extends Annotation> annotationType() {
                return CurrentUserClaims.class;
            }
        };
    }
}
