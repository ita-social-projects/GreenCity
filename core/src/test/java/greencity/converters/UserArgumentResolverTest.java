package greencity.converters;

import static org.mockito.Mockito.when;
import greencity.annotations.CurrentUser;
import greencity.dto.user.UserVO;
import greencity.exception.exceptions.UnauthorizedException;
import greencity.service.UserService;
import java.security.Principal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserArgumentResolverTest {
    @Mock
    private UserService userService;
    @Mock
    private NativeWebRequest webRequest;
    @Mock
    private MethodParameter methodParameter;
    @Mock
    private ModelAndViewContainer mavContainer;
    @InjectMocks
    private UserArgumentResolver userArgumentResolver;
    private static final String EMAIL = "test@example.com";
    private Principal principal;
    private UserVO userVO;

    @BeforeEach
    void setUp() {
        principal = () -> EMAIL;
        userVO = new UserVO();
        userVO.setEmail(EMAIL);
    }

    @Test
    void resolveArgumentShouldReturnUserVOWhenPrincipalExists(){
        when(webRequest.getUserPrincipal()).thenReturn(principal);
        when(userService.findNotDeactivatedByEmail(EMAIL)).thenReturn(userVO);
        Object result = userArgumentResolver.resolveArgument(methodParameter, mavContainer, webRequest, null);
        assertThat(result).isEqualTo(userVO);
        verify(userService).findNotDeactivatedByEmail(EMAIL);
    }

    @Test
    void resolveArgumentShouldThrowUnauthorizedExceptionWhenPrincipalIsNull() {
        when(webRequest.getUserPrincipal()).thenReturn(null);
        assertThrows(UnauthorizedException.class, () ->
            userArgumentResolver.resolveArgument(methodParameter, mavContainer, webRequest, null));
    }

    @Test
    void supportsParameterShouldReturnTrueForUserVO() {
        when(methodParameter.getParameterAnnotation(CurrentUser.class)).thenReturn(mock(CurrentUser.class));
        when(methodParameter.getParameterType()).thenReturn((Class) UserVO.class);
        boolean result = userArgumentResolver.supportsParameter(methodParameter);
        assertThat(result).isTrue();
    }

    @Test
    void supportsParameterShouldReturnFalseWhenAnnotationMissing() {
        when(methodParameter.getParameterAnnotation(CurrentUser.class)).thenReturn(null);
        boolean result = userArgumentResolver.supportsParameter(methodParameter);
        assertThat(result).isFalse();
    }
}