package greencity.validator;

import greencity.annotations.Sortable;
import greencity.constant.ErrorMessage;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import greencity.exception.exceptions.UnsupportedSortException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

/**
 * Validator responsible for ensuring that sorting parameters provided in a
 * request are allowed for a given DTO or entity class.
 * <p>
 * This class inspects the target class for {@link Sortable} annotations and
 * determines which fields are permitted to be used in sorting.
 * </p>
 * <ul>
 *     <li>If the class has a {@link Sortable} annotation with an explicit
 *         {@code fields()} list, only those fields will be allowed.</li>
 *     <li>If the class is annotated with {@link Sortable} but the
 *         {@code fields()} list is empty, the validator will check for
 *         {@link Sortable} annotations on individual fields and use them.</li>
 *     <li>If neither the class nor its fields define explicit sortable
 *         fields, all declared fields of the class will be allowed for
 *         sorting.</li>
 * </ul>
 * <p>
 * To avoid repeated reflection scans, the allowed sortable fields for each
 * class are cached using a {@link ClassValue} keyed by the class type.
 * </p>
 */
@Component
public class SortPageableValidator {
    /**
     * Cache of sortable fields for each class, computed once and reused.
     * The key is the target class; the value is the set of field names
     * permitted for sorting.
     */
    private static final ClassValue<Set<String>> sortableFieldsCache = new ClassValue<>() {
        @Override
        protected Set<String> computeValue(Class<?> type) {
            Sortable classSortable = type.getAnnotation(Sortable.class);
            Set<String> sortableFields = new HashSet<>();

            if (classSortable != null) {
                String[] classLevelSortableFields = classSortable.fields();
                if (classLevelSortableFields.length > 0) {
                    // Class-level annotation explicitly lists sortable fields
                    sortableFields.addAll(Arrays.asList(classLevelSortableFields));
                } else {
                    // No explicit fields — check for @Sortable on individual fields
                    for (Field field : type.getDeclaredFields()) {
                        if (field.isAnnotationPresent(Sortable.class)) {
                            sortableFields.add(field.getName());
                        }
                    }
                    // If still empty, allow all declared fields
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

    /**
     * Validates that all sort properties specified in the given {@link Sort}
     * object are allowed for the specified class.
     * <p>
     * The allowed fields are determined by the presence of {@link Sortable}
     * annotations on the class or its fields, as described in the class-level
     * documentation.
     * </p>
     *
     * @param clazz the class whose sortable fields should be validated against
     * @param sort  the {@link Sort} object containing sort orders from the request
     * @throws UnsupportedSortException if any sort property is not in the
     *                                       allowed set of sortable fields
     */
    public void validate(Class<?> clazz, Sort sort) {
        Set<String> allowedFields = getSortableFields(clazz);

        List<String> invalidFields = sort.stream()
                .map(Sort.Order::getProperty)
                .filter(field -> !allowedFields.contains(field))
                .toList();

        if (!invalidFields.isEmpty()) {
            throw new UnsupportedSortException(ErrorMessage.INVALID_SORTING_VALUE + invalidFields);
        }
    }

    /**
     * Retrieves the cached set of sortable fields for the given class.
     *
     * @param clazz the class to inspect
     * @return an immutable set of field names allowed for sorting
     */
    private Set<String> getSortableFields(Class<?> clazz) {
        return sortableFieldsCache.get(clazz);
    }
}
