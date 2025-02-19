package greencity.handler;

import greencity.TestConst;
import greencity.annotations.ApiPageable;
import greencity.constant.ErrorMessage;
import greencity.dto.friends.UserFriendDto;
import greencity.exception.exceptions.UnsupportedSortException;
import greencity.validator.SortPageableValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortHandlerMethodArgumentResolver;
import org.springframework.web.context.request.NativeWebRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomSortHandlerMethodArgumentResolverTest {

    private CustomSortHandlerMethodArgumentResolver resolver;

    @Mock
    private SortPageableValidator sortPageableValidator;

    @Mock
    private SortHandlerMethodArgumentResolver delegate;

    @Mock
    private MethodParameter methodParameter;

    @Mock
    private NativeWebRequest webRequest;

    @BeforeEach
    void setUp() {
        resolver = new CustomSortHandlerMethodArgumentResolver(sortPageableValidator, delegate);
    }

    @Test
    void supportsParameterShouldReturnTrueForSortTypeTest() {
        when(methodParameter.getParameterType()).thenAnswer(invocation -> Sort.class);
        boolean result = resolver.supportsParameter(methodParameter);
        assertTrue(result);
    }

    @Test
    void supportsParameterShouldReturnFalseForNonSortTypeTest() {
        when(methodParameter.getParameterType()).thenAnswer(invocation -> String.class);
        boolean result = resolver.supportsParameter(methodParameter);
        assertFalse(result);
    }

    @Test
    void supportsParameterShouldReturnFalseForNullParameterTypeTest() {
        when(methodParameter.getParameterType()).thenReturn(null);
        boolean result = resolver.supportsParameter(methodParameter);
        assertFalse(result);
    }

    @Test
    void resolveArgumentShouldReturnSortWhenNoSortingAppliedTest() {
        Sort unsortedSort = Sort.unsorted();
        when(delegate.resolveArgument(methodParameter, null, webRequest, null)).thenReturn(unsortedSort);

        Sort result = resolver.resolveArgument(methodParameter, null, webRequest, null);

        assertEquals(unsortedSort, result);
        verify(sortPageableValidator, never()).validateSortParameter(any(), any());
    }

    @Test
    void resolveArgumentShouldValidateSortWhenSortingIsAppliedAndApiPageablePresentTest() {
        Sort sorted = Sort.by(TestConst.FIELD_NAME).ascending();
        when(delegate.resolveArgument(methodParameter, null, webRequest, null)).thenReturn(sorted);

        ApiPageable apiPageableMock = mock(ApiPageable.class);
        when(apiPageableMock.dtoClass()).thenAnswer(invocation -> UserFriendDto.class);
        when(methodParameter.getMethodAnnotation(ApiPageable.class)).thenReturn(apiPageableMock);

        Sort result = resolver.resolveArgument(methodParameter, null, webRequest, null);

        assertEquals(sorted, result);
        verify(sortPageableValidator).validateSortParameter(UserFriendDto.class, sorted);
    }

    @Test
    void resolveArgumentShouldNotValidateSortWhenNoApiPageableAnnotationTest() {
        Sort sorted = Sort.by(TestConst.FIELD_NAME).ascending();
        when(delegate.resolveArgument(methodParameter, null, webRequest, null)).thenReturn(sorted);
        when(methodParameter.getMethodAnnotation(ApiPageable.class)).thenReturn(null);

        Sort result = resolver.resolveArgument(methodParameter, null, webRequest, null);

        assertEquals(sorted, result);
        verify(sortPageableValidator, never()).validateSortParameter(any(), any());
    }

    @Test
    void resolveArgumentShouldThrowExceptionWhenInvalidSortFieldIsUsedTest() {
        Sort invalidSort = Sort.by(TestConst.FIELD_NAME).ascending();
        when(delegate.resolveArgument(methodParameter, null, webRequest, null)).thenReturn(invalidSort);

        ApiPageable apiPageableMock = mock(ApiPageable.class);
        when(apiPageableMock.dtoClass()).thenAnswer(invocation -> UserFriendDto.class);
        when(methodParameter.getMethodAnnotation(ApiPageable.class)).thenReturn(apiPageableMock);

        doThrow(new UnsupportedSortException(ErrorMessage.INVALID_SORTING_VALUE))
            .when(sortPageableValidator).validateSortParameter(UserFriendDto.class, invalidSort);

        assertThrows(UnsupportedSortException.class, () ->
            resolver.resolveArgument(methodParameter, null, webRequest, null)
        );

        verify(sortPageableValidator).validateSortParameter(UserFriendDto.class, invalidSort);
    }
}