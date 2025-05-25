package greencity.converters;

import greencity.TestConst;
import greencity.annotations.CurrentUserId;
import greencity.constant.ErrorMessage;
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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserIdArgumentResolverTest {

    @Mock
    JwtTool jwtTool;

    @InjectMocks
    UserIdArgumentResolver userIdArgumentResolver;

    @Test
    @SneakyThrows
    void resolveArgumentTest() {
        String jwt = "jwt";
        Long userId = TestConst.USER_ID;
        MethodParameter methodParameter = mock(MethodParameter.class);
        ModelAndViewContainer modelAndViewContainer = mock(ModelAndViewContainer.class);
        NativeWebRequest nativeWebRequest = mock(NativeWebRequest.class);
        WebDataBinderFactory webDataBinderFactory = mock(WebDataBinderFactory.class);

        when(jwtTool.extractJwtFromNativeWebRequest(any(NativeWebRequest.class)))
            .thenReturn(jwt);
        when(jwtTool.extractUserId(jwt))
            .thenReturn(userId);

        Long actualResult = userIdArgumentResolver.resolveArgument(
            methodParameter,
            modelAndViewContainer,
            nativeWebRequest,
            webDataBinderFactory);

        assertEquals(userId, actualResult);
        verify(jwtTool).extractJwtFromNativeWebRequest(any(NativeWebRequest.class));
        verify(jwtTool).extractUserId(jwt);
    }

    @Test
    @SneakyThrows
    void resolveArgumentWhenNoJwtAndAnnotationRequiredTest() {
        String jwt = "jwt";
        MethodParameter methodParameter = mock(MethodParameter.class);
        ModelAndViewContainer modelAndViewContainer = mock(ModelAndViewContainer.class);
        NativeWebRequest nativeWebRequest = mock(NativeWebRequest.class);
        WebDataBinderFactory webDataBinderFactory = mock(WebDataBinderFactory.class);
        CurrentUserId currentUserId = buildCurrentUserId(true);

        when(jwtTool.extractJwtFromNativeWebRequest(any(NativeWebRequest.class)))
            .thenThrow(new NoJwtException());
        when(methodParameter.getParameterAnnotation(CurrentUserId.class))
            .thenReturn(currentUserId);

        assertThrows(
            NoJwtException.class,
            () -> userIdArgumentResolver.resolveArgument(
                methodParameter,
                modelAndViewContainer,
                nativeWebRequest,
                webDataBinderFactory));

        verify(jwtTool).extractJwtFromNativeWebRequest(any(NativeWebRequest.class));
        verify(jwtTool, never()).extractUserId(jwt);
    }

    @Test
    @SneakyThrows
    void resolveArgumentWhenNoJwtAndAnnotationNullTest() {
        String jwt = "jwt";
        MethodParameter methodParameter = mock(MethodParameter.class);
        ModelAndViewContainer modelAndViewContainer = mock(ModelAndViewContainer.class);
        NativeWebRequest nativeWebRequest = mock(NativeWebRequest.class);
        WebDataBinderFactory webDataBinderFactory = mock(WebDataBinderFactory.class);
        CurrentUserId currentUserId = null;
        String expectedExceptionMessage = ErrorMessage.ANNOTATION_ARGUMENT_NOT_SUPPORTED;

        when(jwtTool.extractJwtFromNativeWebRequest(any(NativeWebRequest.class)))
            .thenThrow(new NoJwtException());
        when(methodParameter.getParameterAnnotation(CurrentUserId.class))
            .thenReturn(currentUserId);

        IllegalArgumentException illegalArgumentException = assertThrows(
            IllegalArgumentException.class,
            () -> userIdArgumentResolver.resolveArgument(
                methodParameter,
                modelAndViewContainer,
                nativeWebRequest,
                webDataBinderFactory));

        assertEquals(expectedExceptionMessage, illegalArgumentException.getMessage());
        verify(jwtTool).extractJwtFromNativeWebRequest(any(NativeWebRequest.class));
        verify(jwtTool, never()).extractUserId(jwt);
    }

    @Test
    @SneakyThrows
    void resolveArgumentWhenNoJwtAndAnnotationNotRequiredTest() {
        String jwt = "jwt";
        MethodParameter methodParameter = mock(MethodParameter.class);
        ModelAndViewContainer modelAndViewContainer = mock(ModelAndViewContainer.class);
        NativeWebRequest nativeWebRequest = mock(NativeWebRequest.class);
        WebDataBinderFactory webDataBinderFactory = mock(WebDataBinderFactory.class);
        CurrentUserId currentUserId = buildCurrentUserId(false);

        when(jwtTool.extractJwtFromNativeWebRequest(any(NativeWebRequest.class)))
            .thenThrow(new NoJwtException());
        when(methodParameter.getParameterAnnotation(CurrentUserId.class))
            .thenReturn(currentUserId);

        Long actualResult = userIdArgumentResolver.resolveArgument(
            methodParameter,
            modelAndViewContainer,
            nativeWebRequest,
            webDataBinderFactory);

        assertNull(actualResult);
        verify(jwtTool).extractJwtFromNativeWebRequest(any(NativeWebRequest.class));
        verify(jwtTool, never()).extractUserId(jwt);
    }

    private CurrentUserId buildCurrentUserId(boolean required) {
        return new CurrentUserId() {
            @Override
            public Class<? extends Annotation> annotationType() {
                return CurrentUserId.class;
            }

            @Override
            public boolean required() {
                return required;
            }
        };
    }
}
