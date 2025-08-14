package greencity.config;

import greencity.constant.ErrorMessage;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.MethodParameter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;
import static greencity.constant.PageableConstants.PAGE;
import static greencity.constant.PageableConstants.SIZE;
import static greencity.constant.PageableConstants.MAX_PAGE_SIZE;
import static greencity.constant.PageableConstants.DEFAULT_PAGE_SIZE;
import static greencity.constant.PageableConstants.DEFAULT_PAGE;

/**
 * Custom implementation of Spring Data's
 * {@link org.springframework.data.web.PageableHandlerMethodArgumentResolver}
 * that adds stricter validation rules and integrates with a custom sort
 * resolver.
 * <p>
 * This resolver is responsible for converting HTTP request parameters into a
 * {@link org.springframework.data.domain.Pageable} instance, applying both
 * pagination and sorting logic. It extends the default Spring behavior by:
 * </p>
 * <ul>
 * <li>Parsing and validating {@code page} and {@code size} query
 * parameters.</li>
 * <li>Rejecting negative or non-numeric values for pagination parameters.</li>
 * <li>Enforcing a maximum allowed page size ({@code MAX_PAGE_SIZE}).</li>
 * <li>Delegating sort parameter parsing and validation to
 * {@link CustomSortHandlerMethodArgumentResolver}.</li>
 * </ul>
 *
 * <h2>Supported Query Parameters:</h2>
 * <ul>
 * <li><b>{@code page}</b> – 0-based page index. Defaults to
 * {@code DEFAULT_PAGE} if omitted, but must be greater than or equal to 0. An
 * invalid value will throw an {@link IllegalArgumentException}.</li>
 * <li><b>{@code size}</b> – Number of items per page. Defaults to
 * {@code DEFAULT_PAGE_SIZE} if omitted, but must be ≥ 1 and ≤
 * {@code MAX_PAGE_SIZE}. An invalid value (negative or greater than
 * {@code MAX_PAGE_SIZE}) will throw an {@link IllegalArgumentException}.</li>
 * <li><b>{@code sort}</b> – Sorting instructions, parsed and validated by the
 * {@link CustomSortHandlerMethodArgumentResolver}.</li>
 * </ul>
 *
 * <h2>Validation Rules:</h2>
 * <ul>
 * <li>If {@code size} exceeds {@code MAX_PAGE_SIZE}, a
 * {@link IllegalArgumentException} is thrown with
 * {@link greencity.constant.ErrorMessage#MAX_PAGE_SIZE_EXCEPTION}.</li>
 * <li>If {@code page} or {@code size} is negative, an
 * {@link IllegalArgumentException} is thrown.</li>
 * <li>If {@code page} or {@code size} is non-numeric, an
 * {@link IllegalArgumentException} is thrown.</li>
 * </ul>
 *
 * <h2>Usage:</h2>
 * <p>
 * This class is typically registered as a Spring bean and used automatically by
 * Spring MVC when a controller method has a
 * {@link org.springframework.data.domain.Pageable} parameter. Example:
 * </p>
 *
 * <pre>{@code
 * @GetMapping("/items")
 * public Page<ItemDto> getItems(Pageable pageable) {
 *     return itemService.findAll(pageable);
 * }
 * }</pre>
 *
 * <p>
 * In the above example, requests like:
 * </p>
 *
 * <pre>
 * GET /items?page=1&size=20&sort=name,asc
 * </pre>
 * <p>
 * will be converted into a {@link org.springframework.data.domain.PageRequest}
 * object with validated values and custom sorting rules applied.
 * </p>
 *
 * @see org.springframework.data.web.PageableHandlerMethodArgumentResolver
 * @see CustomSortHandlerMethodArgumentResolver
 */
@Component
@RequiredArgsConstructor
public class CustomPageableHandlerMethodArgumentResolver extends PageableHandlerMethodArgumentResolver {
    /**
     * Custom sort resolver that parses and validates {@code sort} query parameters
     * according to application-specific rules and allowed sortable fields. This is
     * injected and used to handle sorting logic separately from pagination,
     * ensuring that sorting rules can be reused independently of pagination
     * handling.
     */
    private final CustomSortHandlerMethodArgumentResolver customSortHandlerMethodArgumentResolver;

    /**
     * Resolves the {@link Pageable} argument for a controller method by extracting
     * and validating pagination parameters from the HTTP request.
     * <p>
     * This implementation:
     * </p>
     * <ul>
     * <li>Parses {@code page} and {@code size} query parameters, applying defaults
     * if missing.</li>
     * <li>Validates that {@code size} does not exceed {@code MAX_PAGE_SIZE}.</li>
     * <li>Delegates sort resolution to
     * {@link CustomSortHandlerMethodArgumentResolver}.</li>
     * </ul>
     *
     * @param methodParameter the method parameter that should be resolved
     * @param mavContainer    the ModelAndViewContainer for the current request
     * @param webRequest      the current web request providing access to query
     *                        parameters
     * @param binderFactory   the factory for creating WebDataBinders
     * @return a fully configured {@link Pageable} instance with validated
     *         pagination and sorting
     * @throws IllegalArgumentException if {@code page} or {@code size} is less than
     *                                  0, not a valid number, or if {@code size}
     *                                  exceeds the {@code MAX_PAGE_SIZE}.
     */
    @NotNull
    @Override
    public Pageable resolveArgument(@NotNull MethodParameter methodParameter,
        ModelAndViewContainer mavContainer,
        @NotNull NativeWebRequest webRequest,
        WebDataBinderFactory binderFactory) {
        int page = parseParameter(webRequest, PAGE, DEFAULT_PAGE);
        int size = parseParameter(webRequest, SIZE, DEFAULT_PAGE_SIZE);

        if (size < 1) {
            throw new IllegalArgumentException(ErrorMessage.MIN_PAGE_SIZE_EXCEPTION);
        }
        if (size > MAX_PAGE_SIZE) {
            throw new IllegalArgumentException(ErrorMessage.MAX_PAGE_SIZE_EXCEPTION);
        }

        Sort sort = customSortHandlerMethodArgumentResolver.resolveArgument(methodParameter, mavContainer, webRequest,
            binderFactory);

        return PageRequest.of(page, size, sort);
    }

    /**
     * Parses and validates a single pagination parameter from the HTTP request.
     * <p>
     * If the parameter is absent, the provided {@code defaultValue} is returned. If
     * present, the value is parsed as an integer and validated to ensure it is
     * non-negative.
     * </p>
     *
     * @param webRequest   the current web request
     * @param param        the name of the query parameter to parse (e.g.,
     *                     {@code "page"} or {@code "size"})
     * @param defaultValue the default value to return if the parameter is absent
     * @return the parsed integer value of the parameter, or {@code defaultValue} if
     *         not provided
     * @throws IllegalArgumentException if the parameter value is negative or not a
     *                                  valid integer
     */
    private int parseParameter(NativeWebRequest webRequest, String param, int defaultValue) {
        String paramValue = webRequest.getParameter(param);
        if (paramValue == null) {
            return defaultValue;
        }
        try {
            int value = Integer.parseInt(paramValue);

            if (value < 0) {
                throw new IllegalArgumentException(String.format(ErrorMessage.NEGATIVE_VALUE_EXCEPTION, param));
            }

            return value;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(String.format(ErrorMessage.INVALID_VALUE_EXCEPTION, param), e);
        }
    }
}