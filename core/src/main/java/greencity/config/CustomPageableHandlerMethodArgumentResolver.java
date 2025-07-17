package greencity.config;

import greencity.constant.ErrorMessage;
import greencity.constant.PageableConstants;
import greencity.exception.exceptions.BadRequestException;
import org.springframework.core.MethodParameter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;
import java.util.ArrayList;
import java.util.List;
import static greencity.constant.PageableConstants.PAGE;
import static greencity.constant.PageableConstants.SIZE;
import static greencity.constant.PageableConstants.SORT;
import static greencity.constant.PageableConstants.MAX_PAGE_SIZE;
import static greencity.constant.PageableConstants.DEFAULT_PAGE_SIZE;
import static greencity.constant.PageableConstants.DEFAULT_PAGE;
import static greencity.constant.PageableConstants.DEFAULT_SORT;

public class CustomPageableHandlerMethodArgumentResolver extends PageableHandlerMethodArgumentResolver {
    @Override
    public Pageable resolveArgument(MethodParameter methodParameter,
        ModelAndViewContainer mavContainer,
        NativeWebRequest webRequest,
        WebDataBinderFactory binderFactory) {
        int page = parseParameter(webRequest, PAGE, DEFAULT_PAGE);
        int size = parseParameter(webRequest, SIZE, DEFAULT_PAGE_SIZE);
        Sort sort = parseSortParameter(webRequest);

        if (size > MAX_PAGE_SIZE) {
            throw new BadRequestException(ErrorMessage.MAX_PAGE_SIZE_EXCEPTION);
        }

        return PageRequest.of(page, size, sort);
    }

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

    private Sort parseSortParameter(NativeWebRequest webRequest) {
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