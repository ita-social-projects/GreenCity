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

@Component
public class SortPageableValidator {
    private static final ClassValue<Set<String>> sortableFieldsCache = new ClassValue<>() {
        @Override
        protected Set<String> computeValue(Class<?> type) {
            return Arrays.stream(type.getDeclaredFields())
                .peek(field -> field.setAccessible(true))
                .filter(field -> field.isAnnotationPresent(SortableField.class))
                .map(Field::getName)
                .collect(Collectors.toSet());
        }
    };

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

