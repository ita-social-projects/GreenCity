package greencity.config;

import greencity.annotations.ApiPageable;
import greencity.annotations.ApiPageableWithLocale;
import greencity.constant.ErrorMessage;
import greencity.constant.PageableConstants;
import greencity.validator.SortPageableValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.core.MethodParameter;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortArgumentResolver;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;
import java.util.ArrayList;
import java.util.List;
import static greencity.constant.PageableConstants.DEFAULT_SORT;
import static greencity.constant.PageableConstants.SORT;

/**
 * Custom implementation of {@link SortArgumentResolver} that parses and
 * validates {@code sort} query parameters from HTTP requests.
 * <p>
 * This resolver:
 * </p>
 * <ul>
 * <li>Supports {@link Sort} method parameters in controller endpoints.</li>
 * <li>Parses multiple sorting criteria from query parameters of the format:
 * {@code property,(asc|desc)}.</li>
 * <li>Applies default ascending order if the direction is omitted.</li>
 * <li>Delegates validation of sortable fields to {@link SortPageableValidator}
 * if the controller method is annotated with {@link ApiPageable}.</li>
 * <li>Throws {@link IllegalArgumentException} for invalid sort formats.</li>
 * </ul>
 *
 * <h2>Example Usage:</h2>
 *
 * <pre>{@code
 * @GetMapping("/users")
 * public Page<UserDto> getUsers(Pageable pageable, Sort sort) {
 *     // sort parameter is automatically parsed and validated
 * }
 * }</pre>
 *
 * @see SortArgumentResolver
 * @see SortPageableValidator
 * @see ApiPageable
 */
@Component
@RequiredArgsConstructor
public class CustomSortHandlerMethodArgumentResolver implements SortArgumentResolver {
    /**
     * Validator for ensuring that only allowed fields are used for sorting.
     * <p>
     * This validator is invoked when a controller method is annotated with
     * {@link ApiPageable}, checking that the {@link Sort} object only contains
     * properties declared as sortable.
     * </p>
     */
    private final SortPageableValidator sortPageableValidator;

    /**
     * Indicates whether this resolver supports the given method parameter.
     *
     * @param parameter the method parameter to check
     * @return {@code true} if the parameter type is {@link Sort}, {@code false}
     *         otherwise
     */
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return Sort.class.equals(parameter.getParameterType());
    }

    /**
     * Resolves the {@link Sort} argument for a controller method by parsing the
     * "sort" query parameters from the HTTP request.
     * <p>
     * If the controller method is annotated with {@link ApiPageable} or
     * {@link ApiPageableWithLocale}, the resolved {@link Sort} object is validated
     * using {@link SortPageableValidator}.
     * </p>
     *
     * @param parameter     the method parameter to resolve
     * @param mavContainer  the current ModelAndViewContainer, may be {@code null}
     * @param webRequest    the current web request providing access to query
     *                      parameters
     * @param binderFactory a factory for creating WebDataBinders, may be
     *                      {@code null}
     * @return a {@link Sort} instance containing the parsed sorting orders
     * @throws IllegalArgumentException if any sort parameter is invalid or
     *                                  incorrectly formatted
     */
    @NonNull
    @Override
    public Sort resolveArgument(@NonNull MethodParameter parameter, @Nullable ModelAndViewContainer mavContainer,
        @NonNull NativeWebRequest webRequest, @Nullable WebDataBinderFactory binderFactory) {
        Sort sort = parseParameter(webRequest);

        ApiPageable apiPageable = parameter.getMethodAnnotation(ApiPageable.class);
        ApiPageableWithLocale apiPageableWithLocale = parameter.getMethodAnnotation(ApiPageableWithLocale.class);

        Class<?> clazz = null;

        if (sort.isSorted()) {
            if (apiPageable != null) {
                clazz = apiPageable.clazz();
            } else if (apiPageableWithLocale != null) {
                clazz = apiPageableWithLocale.clazz();
            }

            if (clazz != null) {
                sortPageableValidator.validate(clazz, sort);
            }
        }

        return sort;
    }

    /**
     * Parses the "sort" query parameters from the HTTP request and converts them
     * into a {@link Sort} object.
     * <p>
     * Each parameter should be in the format {@code property,(asc|desc)}. If the
     * direction is omitted, ascending order is assumed. Multiple sort parameters
     * can be provided by repeating the query parameter.
     * </p>
     *
     * @param webRequest the current web request
     * @return a {@link Sort} object containing the parsed sort orders, or
     *         {@link PageableConstants#DEFAULT_SORT} if none are provided
     * @throws IllegalArgumentException if a sort parameter is invalid or contains
     *                                  more than one comma
     */
    public Sort parseParameter(NativeWebRequest webRequest) {
        String[] sortParams = webRequest.getParameterValues(SORT);
        if (sortParams == null || sortParams.length == 0) {
            return DEFAULT_SORT;
        }

        List<Sort.Order> orders = new ArrayList<>();
        for (String sortParam : sortParams) {
            if (sortParam == null || sortParam.isEmpty()) {
                continue;
            }

            String[] parts = sortParam.split(",");
            if (parts.length == 2) {
                String property = parts[0].trim();
                String direction = parts[1].trim();
                Sort.Direction sortDirection = Sort.Direction.fromString(direction);
                orders.add(new Sort.Order(sortDirection, property));
            } else if (parts.length == 1) {
                orders.add(new Sort.Order(Sort.Direction.ASC, parts[0].trim()));
            } else {
                throw new IllegalArgumentException(
                    String.format(ErrorMessage.INVALID_SORT_FORMAT_EXCEPTION, sortParam));
            }
        }

        return orders.isEmpty() ? PageableConstants.DEFAULT_SORT : Sort.by(orders);
    }
}
