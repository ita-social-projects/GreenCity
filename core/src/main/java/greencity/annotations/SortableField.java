package greencity.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to mark a field as sortable in a DTO.
 * <p>
 * Fields annotated with {@code @SortableField} can be used in sorting
 * when handling pageable API requests.
 * </p>
 * <p> The validation mechanism ensures that only fields with this annotation
 * are allowed in sorting parameters.</p>
 **/
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SortableField {
}
