package greencity.config;

import greencity.annotations.ApiPageable;
import greencity.constant.ErrorMessage;
import greencity.validator.SortPageableValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.core.MethodParameter;
import org.springframework.data.domain.Sort;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.context.request.NativeWebRequest;
import static greencity.constant.PageableConstants.DEFAULT_SORT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomSortHandlerMethodArgumentResolverTest {

    private CustomSortHandlerMethodArgumentResolver resolver;

    @Mock
    private NativeWebRequest webRequest;

    @Mock
    private SortPageableValidator sortPageableValidator;

    @BeforeEach
    void setUp() {
        resolver = new CustomSortHandlerMethodArgumentResolver(sortPageableValidator);
    }

    @Test
    void shouldReturnDefaultSortWhenNoSortParametersProvided() {
        when(webRequest.getParameterValues("sort")).thenReturn(null);

        MethodParameter methodParameter = mock(MethodParameter.class);

        Sort sort = resolver.resolveArgument(methodParameter, null, webRequest, null);

        assertEquals(DEFAULT_SORT, sort);
    }

    @Test
    void shouldParseSingleSortParameterCorrectly() {
        when(webRequest.getParameterValues("sort")).thenReturn(new String[]{"name,asc"});

        MethodParameter methodParameter = mock(MethodParameter.class);

        Sort sort = resolver.resolveArgument(methodParameter, null, webRequest, null);

        assertEquals(Sort.by(Sort.Order.asc("name")), sort);
    }

    @Test
    void shouldParseDescendingSortParameterCorrectly() {
        when(webRequest.getParameterValues("sort")).thenReturn(new String[]{"name,desc"});

        MethodParameter methodParameter = mock(MethodParameter.class);

        Sort sort = resolver.resolveArgument(methodParameter, null, webRequest, null);

        assertEquals(Sort.by(Sort.Order.desc("name")), sort);
    }

    @Test
    void shouldParseSortWithMultipleParametersCorrectly() {
        when(webRequest.getParameterValues("sort")).thenReturn(new String[]{"name,asc", "age,desc"});

        MethodParameter methodParameter = mock(MethodParameter.class);

        Sort sort = resolver.resolveArgument(methodParameter, null, webRequest, null);

        assertEquals(Sort.by(Sort.Order.asc("name"), Sort.Order.desc("age")), sort);
    }

    @Test
    void shouldReturnSortWithDefaultDirectionWhenNoDirectionProvided() {
        when(webRequest.getParameterValues("sort")).thenReturn(new String[]{"name"});

        MethodParameter methodParameter = mock(MethodParameter.class);

        Sort sort = resolver.resolveArgument(methodParameter, null, webRequest, null);

        assertEquals(Sort.by(Sort.Order.asc("name")), sort);
    }

    @Test
    void shouldThrowExceptionForInvalidSortFormat() {
        when(webRequest.getParameterValues("sort")).thenReturn(new String[]{"title,asc,extra"});

        MethodParameter methodParameter = mock(MethodParameter.class);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                resolver.resolveArgument(methodParameter, null, webRequest, null));

        assertEquals(String.format(ErrorMessage.INVALID_SORT_FORMAT_EXCEPTION, "title,asc,extra"), exception.getMessage());
    }

    @Test
    void shouldThrowExceptionForInvalidSortDirection() {
        when(webRequest.getParameterValues("sort")).thenReturn(new String[]{"title,wrong"});

        MethodParameter methodParameter = mock(MethodParameter.class);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                resolver.resolveArgument(methodParameter, null, webRequest, null));

        String expectedMessagePart = "Invalid value 'wrong' for orders given";
        assertTrue(exception.getMessage().contains(expectedMessagePart));
    }

    @Test
    @SuppressWarnings("unchecked")
    void shouldValidateSortableFieldsWithApiPageableAnnotation() {
        when(webRequest.getParameterValues("sort")).thenReturn(new String[]{"name,asc"});

        MethodParameter methodParameter = mock(MethodParameter.class);
        ApiPageable apiPageable = mock(ApiPageable.class);

        when(methodParameter.getMethodAnnotation(ApiPageable.class)).thenReturn(apiPageable);
        when(apiPageable.clazz()).thenReturn((Class) String.class);

        resolver.resolveArgument(methodParameter, null, webRequest, null);

        verify(sortPageableValidator).validate(any(), any());
    }

    @Test
    void shouldThrowExceptionWhenSortParameterHasMoreThanOneComma() {
        when(webRequest.getParameterValues("sort")).thenReturn(new String[]{"name,asc,extra"});

        MethodParameter methodParameter = mock(MethodParameter.class);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                resolver.resolveArgument(methodParameter, null, webRequest, null));

        assertEquals(String.format(ErrorMessage.INVALID_SORT_FORMAT_EXCEPTION, "name,asc,extra"), exception.getMessage());
    }

    @Test
    void shouldReturnDefaultSortWhenEmptySortParameterProvided() {
        when(webRequest.getParameterValues("sort")).thenReturn(new String[]{""});

        MethodParameter methodParameter = mock(MethodParameter.class);

        Sort sort = resolver.resolveArgument(methodParameter, null, webRequest, null);

        assertEquals(DEFAULT_SORT, sort);
    }

    @Test
    @SuppressWarnings("unchecked")
    void shouldCallValidatorWhenSortIsProvidedAndApiPageablePresent() {
        when(webRequest.getParameterValues("sort")).thenReturn(new String[]{"name,asc"});
        MethodParameter methodParameter = mock(MethodParameter.class);
        ApiPageable apiPageable = mock(ApiPageable.class);

        when(methodParameter.getMethodAnnotation(ApiPageable.class)).thenReturn(apiPageable);

        when(apiPageable.clazz()).thenReturn((Class) String.class);

        Sort sort = resolver.resolveArgument(methodParameter, null, webRequest, null);

        verify(sortPageableValidator).validate(String.class, sort);
        assertEquals(Sort.by(Sort.Order.asc("name")), sort);
    }

    @Test
    void shouldNotCallValidatorWhenSortIsNotProvidedEvenIfApiPageablePresent() {
        when(webRequest.getParameterValues("sort")).thenReturn(null);
        MethodParameter methodParameter = mock(MethodParameter.class);
        ApiPageable apiPageable = mock(ApiPageable.class);

        when(methodParameter.getMethodAnnotation(ApiPageable.class)).thenReturn(apiPageable);

        Sort sort = resolver.resolveArgument(methodParameter, null, webRequest, null);

        verify(sortPageableValidator, never()).validate(any(), any());
        assertEquals(DEFAULT_SORT, sort);
    }

    @Test
    void shouldNotCallValidatorWhenApiPageableIsNullEvenIfSortProvided() {
        when(webRequest.getParameterValues("sort")).thenReturn(new String[]{"name,asc"});
        MethodParameter methodParameter = mock(MethodParameter.class);

        when(methodParameter.getMethodAnnotation(ApiPageable.class)).thenReturn(null);

        Sort sort = resolver.resolveArgument(methodParameter, null, webRequest, null);

        verify(sortPageableValidator, never()).validate(any(), any());
        assertEquals(Sort.by(Sort.Order.asc("name")), sort);
    }
}
