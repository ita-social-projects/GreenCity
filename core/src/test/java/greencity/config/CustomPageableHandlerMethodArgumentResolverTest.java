package greencity.config;

import greencity.constant.ErrorMessage;
import greencity.exception.exceptions.BadRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.context.request.NativeWebRequest;
import static greencity.constant.PageableConstants.DEFAULT_PAGE;
import static greencity.constant.PageableConstants.DEFAULT_SORT;
import static greencity.constant.PageableConstants.DEFAULT_PAGE_SIZE;
import static greencity.constant.PageableConstants.PAGE;
import static greencity.constant.PageableConstants.SIZE;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomPageableHandlerMethodArgumentResolverTest {
    private CustomPageableHandlerMethodArgumentResolver resolver;

    @Mock
    private NativeWebRequest webRequest;

    @Mock
    private CustomSortHandlerMethodArgumentResolver customSortHandlerMethodArgumentResolver;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        resolver = new CustomPageableHandlerMethodArgumentResolver(customSortHandlerMethodArgumentResolver);
    }

    @Test
    void shouldReturnDefaultPageableWhenNoParametersProvidedTest() {
        when(webRequest.getParameter(PAGE)).thenReturn(null);
        when(webRequest.getParameter(SIZE)).thenReturn(null);

        Pageable pageable = resolver.resolveArgument(null, null, webRequest, null);

        assertEquals(PageRequest.of(DEFAULT_PAGE, DEFAULT_PAGE_SIZE), pageable);
    }

    @Test
    void shouldReturnCustomPageableWhenValidParametersProvidedTest() {
        when(webRequest.getParameter("page")).thenReturn("2");
        when(webRequest.getParameter("size")).thenReturn("10");

        Pageable pageable = resolver.resolveArgument(null, null, webRequest, null);

        assertEquals(PageRequest.of(2, 10), pageable);
    }

    @Test
    void shouldThrowExceptionWhenPageIsNegativeTest() {
        when(webRequest.getParameter(PAGE)).thenReturn("-1");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                resolver.resolveArgument(null, null, webRequest, null));

        assertEquals(String.format(ErrorMessage.NEGATIVE_VALUE_EXCEPTION,"page"),
                exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenSizeIsNegativeTest() {
        when(webRequest.getParameter(PAGE)).thenReturn("1");
        when(webRequest.getParameter(SIZE)).thenReturn("-10");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                resolver.resolveArgument(null, null, webRequest, null));

        assertEquals(String.format(ErrorMessage.NEGATIVE_VALUE_EXCEPTION,"size"),
                exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenSizeExceedsMaxLimitTest() {
        when(webRequest.getParameter(PAGE)).thenReturn("2");
        when(webRequest.getParameter(SIZE)).thenReturn("200");

        BadRequestException exception = assertThrows(BadRequestException.class, () ->
                resolver.resolveArgument(null, null, webRequest, null));

        assertEquals(ErrorMessage.MAX_PAGE_SIZE_EXCEPTION, exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenPageIsInvalidTest() {
        when(webRequest.getParameter("page")).thenReturn("abc");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                resolver.resolveArgument(null, null, webRequest, null));

        assertEquals(String.format(ErrorMessage.INVALID_VALUE_EXCEPTION, "page"),
                exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenSizeInvalidTest(){
        when(webRequest.getParameter(PAGE)).thenReturn("2");
        when(webRequest.getParameter(SIZE)).thenReturn("abc");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                resolver.resolveArgument(null, null, webRequest, null));

        assertEquals(String.format(ErrorMessage.INVALID_VALUE_EXCEPTION, "size"), exception.getMessage());
    }

    @Test
    void shouldUseDefaultSortWhenSortNotProvidedTest() {
        when(webRequest.getParameter(PAGE)).thenReturn("0");
        when(webRequest.getParameter(SIZE)).thenReturn("10");
        when(webRequest.getParameterValues("sort")).thenReturn(null);

        Pageable pageable = resolver.resolveArgument(null, null, webRequest, null);

        assertEquals(PageRequest.of(0, 10, DEFAULT_SORT), pageable);
    }

    @Test
    void shouldParseSingleSortWithDirectionTest() {
        when(webRequest.getParameter(PAGE)).thenReturn("0");
        when(webRequest.getParameter(SIZE)).thenReturn("10");
        when(webRequest.getParameterValues("sort")).thenReturn(new String[]{"title,desc"});

        Pageable pageable = resolver.resolveArgument(null, null, webRequest, null);

        assertEquals(Sort.by(Order.desc("title")), pageable.getSort());
    }

    @Test
    void shouldParseMultipleSortsTest() {
        when(webRequest.getParameter(PAGE)).thenReturn("0");
        when(webRequest.getParameter(SIZE)).thenReturn("10");
        when(webRequest.getParameterValues("sort")).thenReturn(new String[]{"title,desc", "createdDate,asc"});

        Pageable pageable = resolver.resolveArgument(null, null, webRequest, null);

        assertEquals(Sort.by(Order.desc("title"), Order.asc("createdDate")), pageable.getSort());
    }

    @Test
    void shouldDefaultToAscIfDirectionNotProvidedTest() {
        when(webRequest.getParameter(PAGE)).thenReturn("0");
        when(webRequest.getParameter(SIZE)).thenReturn("10");
        when(webRequest.getParameterValues("sort")).thenReturn(new String[]{"title"});

        Pageable pageable = resolver.resolveArgument(null, null, webRequest, null);

        assertEquals(Sort.by(Order.asc("title")), pageable.getSort());
    }

    @Test
    void shouldThrowExceptionForInvalidSortDirectionTest() {
        when(webRequest.getParameter(PAGE)).thenReturn("0");
        when(webRequest.getParameter(SIZE)).thenReturn("10");
        when(webRequest.getParameterValues("sort")).thenReturn(new String[]{"title,wrong"});

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                resolver.resolveArgument(null, null, webRequest, null));

        String expectedMessagePart = "Invalid value 'wrong' for orders given";
        assertTrue(exception.getMessage().contains(expectedMessagePart));
    }

    @Test
    void shouldThrowExceptionForInvalidSortFormatTest() {
        when(webRequest.getParameter(PAGE)).thenReturn("0");
        when(webRequest.getParameter(SIZE)).thenReturn("10");
        when(webRequest.getParameterValues("sort")).thenReturn(new String[]{"title,asc,extra"});

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                resolver.resolveArgument(null, null, webRequest, null));

        assertEquals(String.format(ErrorMessage.INVALID_SORT_FORMAT_EXCEPTION, "title,asc,extra"), exception.getMessage());
    }

    @Test
    void shouldSkipEmptySortAndReturnDefaultSortTest() {
        when(webRequest.getParameter(PAGE)).thenReturn("0");
        when(webRequest.getParameter(SIZE)).thenReturn("10");
        when(webRequest.getParameterValues("sort")).thenReturn(new String[]{""});

        Pageable pageable = resolver.resolveArgument(null, null, webRequest, null);

        assertEquals(DEFAULT_SORT, pageable.getSort());
    }

}
