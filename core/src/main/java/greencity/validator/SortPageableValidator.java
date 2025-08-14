package greencity.validator;

import greencity.annotations.Sortable;
import greencity.constant.ErrorMessage;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

@Component
public class SortPageableValidator {
    private static final ClassValue<Set<String>> sortableFieldsCache = new ClassValue<>() {
        @Override
        protected Set<String> computeValue(Class<?> type) {
            Sortable classSortable = type.getAnnotation(Sortable.class);
            Set<String> sortableFields = new HashSet<>();

            if (classSortable != null) {
                String[] classLevelSortableFields = classSortable.fields();
                if (classLevelSortableFields.length > 0) {
                    sortableFields.addAll(Arrays.asList(classLevelSortableFields));
                } else {
                    for (Field field : type.getDeclaredFields()) {
                        if (field.isAnnotationPresent(Sortable.class)) {
                            sortableFields.add(field.getName());
                        }
                    }

                    if (sortableFields.isEmpty()) {
                        for (Field field : type.getDeclaredFields()) {
                            sortableFields.add(field.getName());
                        }
                    }
                }
            }

            return sortableFields;
        }
    };

    public void validateSortParameter(Class<?> clazz, Sort sort) {
        Set<String> allowedFields = getSortableFields(clazz);

        List<String> invalidFields = sort.stream()
                .map(Sort.Order::getProperty)
                .filter(field -> !allowedFields.contains(field))
                .toList();

        if (!invalidFields.isEmpty()) {
            throw new RuntimeException(ErrorMessage.INVALID_SORTING_VALUE + invalidFields);
        }
    }

    private Set<String> getSortableFields(Class<?> clazz) {
        return sortableFieldsCache.get(clazz);
    }
}
