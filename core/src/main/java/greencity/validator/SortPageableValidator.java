package greencity.validator;

import greencity.annotations.SortableField;
import greencity.constant.ErrorMessage;
import greencity.dto.SortableDTO;
import greencity.exception.exceptions.UnsupportedSortException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

/**
 * Validator that checks whether sorting parameters provided in API requests are
 * valid for a given DTO class.
 * <p>
 * Uses a {@link ClassValue} cache to store sortable field names, improving
 * performance by avoiding repeated reflection calls.
 * </p>
 */
@Component
public class SortPageableValidator {
    private static final ClassValue<Set<String>> sortableFieldsCache = new ClassValue<>() {
        @Override
        protected Set<String> computeValue(Class<?> type) {
            return Arrays.stream(type.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(SortableField.class))
                .map(Field::getName)
                .collect(Collectors.toSet());
        }
    };

    /**
     * Validates whether the provided sorting parameters are allowed for the given
     * DTO class.
     *
     * @param dtoClass The DTO class that defines the valid sortable fields.
     * @param sort     The sort object containing sorting parameters.
     * @throws UnsupportedSortException If any sorting field is not allowed.
     */
    public void validateSortParameter(Class<? extends SortableDTO> dtoClass, Sort sort) {
        Set<String> allowedFields = getSortableFields(dtoClass);

        List<String> invalidFields = sort.stream()
            .map(Sort.Order::getProperty)
            .filter(field -> !allowedFields.contains(field))
            .toList();

        if (!invalidFields.isEmpty()) {
            throw new UnsupportedSortException(ErrorMessage.INVALID_SORTING_VALUE + invalidFields);
        }
    }

    private Set<String> getSortableFields(Class<? extends SortableDTO> dtoClass) {
        return sortableFieldsCache.get(dtoClass);
    }
}
