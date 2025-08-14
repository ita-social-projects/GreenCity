package greencity.validator;

import greencity.annotations.Sortable;
import greencity.constant.ErrorMessage;
import greencity.exception.exceptions.UnsupportedSortException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link SortPageableValidator}.
 * <p>
 * These tests verify the behavior of the validator which ensures that the sort
 * parameters in a {@link Sort} object conform to the allowed sortable fields
 * defined by the {@link Sortable} annotations on a target class.
 * </p>
 * <p>
 * The test covers the following scenarios:
 * <ul>
 * <li>Classes with {@code @Sortable(fields = {...})} at class level.</li>
 * <li>Classes with {@code @Sortable} at class level and on individual
 * fields.</li>
 * <li>Classes with {@code @Sortable} but no field-level annotations, allowing
 * all fields.</li>
 * <li>Classes with no {@code @Sortable} annotations at all.</li>
 * <li>Empty {@link Sort} instances.</li>
 * <li>Validation failure cases for invalid sort fields.</li>
 * </ul>
 * </p>
 */
@ExtendWith(MockitoExtension.class)
class SortPageableValidatorTest {
    private SortPageableValidator validator;

    /**
     * Initializes a new instance of {@link SortPageableValidator} before each test.
     */
    @BeforeEach
    void setUp() {
        validator = new SortPageableValidator();
    }

    /**
     * Validates that sorting by fields explicitly listed on the class-level
     * {@link Sortable} annotation passes without errors.
     */
    @Test
    void shouldPassWhenSortFieldsMatchClassLevelAnnotation() {
        Sort sort = Sort.by("name").ascending().and(Sort.by("age").descending());
        assertDoesNotThrow(() -> validator.validate(ClassWithClassLevelSortable.class, sort));
    }

    /**
     * Validates that sorting by a field not listed on the class-level
     * {@link Sortable} annotation causes a validation failure with an appropriate
     * exception.
     */
    @Test
    void shouldFailWhenSortFieldNotInClassLevelAnnotation() {
        Sort sort = Sort.by("city").ascending();
        UnsupportedSortException ex = assertThrows(UnsupportedSortException.class,
            () -> validator.validate(ClassWithClassLevelSortable.class, sort));
        assertTrue(ex.getMessage().contains(String.format(ErrorMessage.INVALID_SORTING_VALUE, "[city]")));
    }

    /**
     * Validates that sorting by fields annotated individually on fields with
     * {@link Sortable} passes validation when the class-level annotation has empty
     * fields().
     */
    @Test
    void shouldPassWhenSortFieldsMatchFieldLevelAnnotation() {
        Sort sort = Sort.by("name").ascending().and(Sort.by("age").descending());
        assertDoesNotThrow(() -> validator.validate(ClassWithFieldLevelSortable.class, sort));
    }

    /**
     * Validates that sorting by a field not annotated with {@link Sortable} on
     * individual fields causes a validation failure.
     */
    @Test
    void shouldFailWhenSortFieldNotInFieldLevelAnnotation() {
        Sort sort = Sort.by("city").ascending();
        UnsupportedSortException ex = assertThrows(UnsupportedSortException.class,
            () -> validator.validate(ClassWithFieldLevelSortable.class, sort));
        assertTrue(ex.getMessage().contains(String.format(ErrorMessage.INVALID_SORTING_VALUE, "[city]")));
    }

    /**
     * Validates that when the class-level {@link Sortable} annotation has empty
     * fields() and no fields are annotated individually, all declared fields are
     * allowed to be sorted.
     */
    @Test
    void shouldPassWhenAllFieldsAllowedIfNoFieldAnnotationsPresent() {
        Sort sort = Sort.by("name").ascending().and(Sort.by("city").descending()).and(Sort.by("age").ascending());
        assertDoesNotThrow(() -> validator.validate(ClassWithoutAnyFieldAnnotations.class, sort));
    }

    /**
     * Validates that when there is no {@link Sortable} annotation present on the
     * class, the validator disallows all sort fields and fails validation.
     */
    @Test
    void shouldFailWhenSortFieldNotPresentInClassWithNoSortableAnnotation() {
        Sort sort = Sort.by("name");
        UnsupportedSortException ex = assertThrows(UnsupportedSortException.class,
            () -> validator.validate(ClassWithoutSortableAnnotation.class, sort));
        assertTrue(ex.getMessage().contains(String.format(ErrorMessage.INVALID_SORTING_VALUE, "[name]")));
    }

    /**
     * Validates that an empty {@link Sort} (no sorting orders) should always pass
     * validation regardless of the sortable fields defined on the class.
     */
    @Test
    void shouldPassWhenSortIsEmpty() {
        Sort sort = Sort.unsorted(); // empty sort
        assertDoesNotThrow(() -> validator.validate(ClassWithClassLevelSortable.class, sort));
        assertDoesNotThrow(() -> validator.validate(ClassWithFieldLevelSortable.class, sort));
        assertDoesNotThrow(() -> validator.validate(ClassWithoutAnyFieldAnnotations.class, sort));
    }

    // Test classes used for validation scenarios

    @SuppressWarnings("unused")
    @Sortable(fields = {"name", "age"})
    public static class ClassWithClassLevelSortable {
        public String name = "test";
        public int age = 1;
        public String city = "Kyiv";
    }

    @SuppressWarnings("unused")
    @Sortable
    public static class ClassWithFieldLevelSortable {
        @Sortable
        public String name = "test";
        @Sortable
        public int age = 1;
        public String city = "Kyiv";
    }

    @SuppressWarnings("unused")
    @Sortable
    public static class ClassWithoutAnyFieldAnnotations {
        public String name = "test";
        public int age = 1;
        public String city = "Kyiv";
    }

    @SuppressWarnings("unused")
    public static class ClassWithoutSortableAnnotation {
        public String name = "test";
        public int age = 1;
        public String city = "Kyiv";
    }
}
